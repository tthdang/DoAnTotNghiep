package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.BatchRequest;
import com.restaurant.BeefChefBackend.dto.response.BatchResponse;
import com.restaurant.BeefChefBackend.entity.Ingredient;
import com.restaurant.BeefChefBackend.entity.IngredientBatch;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.entity.Recipe;
import com.restaurant.BeefChefBackend.enums.BatchStatus;
import com.restaurant.BeefChefBackend.repository.IngredientBatchRepository;
import com.restaurant.BeefChefBackend.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IngredientBatchService {
    @Autowired
    private IngredientBatchRepository ingredientBatchRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    private BatchResponse toResponse(IngredientBatch ingredientBatch){
        return BatchResponse.builder()
                .batchId(ingredientBatch.getBatchId())
                .ingredientId(ingredientBatch.getIngredient().getIngredientId())
                .ingredientName(ingredientBatch.getIngredient().getIngredientName())
                .quantityImported(ingredientBatch.getQuantityImported())
                .quantityRemaining(ingredientBatch.getQuantityRemaining())
                .importDate(ingredientBatch.getImportDate())
                .expiryDate(ingredientBatch.getExpiryDate())
                .batchPrice(ingredientBatch.getBatchPrice())
                .status(ingredientBatch.getStatus())
                .build();
    }

    //create
    public BatchResponse create(BatchRequest request){
        IngredientBatch ingredientBatch = new IngredientBatch();

        Ingredient ingredient = ingredientRepository.findById(request.getIngredientId()).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy nguyên liệu!")
        );

        ingredientBatch.setIngredient(ingredient);
        ingredientBatch.setQuantityImported(request.getQuantityImported());
        ingredientBatch.setQuantityRemaining(request.getQuantityImported());
        ingredientBatch.setImportDate(LocalDate.now());
        ingredientBatch.setExpiryDate(request.getExpiryDate());
        ingredientBatch.setBatchPrice(request.getBatchPrice());
        ingredientBatch.setStatus(BatchStatus.AVAILABLE);

        IngredientBatch save = ingredientBatchRepository.save(ingredientBatch);

        return toResponse(save);
    }

    public List<BatchResponse> getAll(){
        return ingredientBatchRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BatchResponse getById(Integer id){
        IngredientBatch ingredientBatch = ingredientBatchRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy lô nguyên liệu: " + id)
        );

        return toResponse(ingredientBatch);
    }


    //Trừ nguyên liệu
    @Transactional
    public void deductIngredient(Integer ingredientId, double ingredientNeeded) {
        List<IngredientBatch> list = ingredientBatchRepository.findByIngredient_IngredientIdAndQuantityRemainingGreaterThanAndExpiryDateAfterOrderByExpiryDateAsc(
                                ingredientId, 0.0, LocalDate.now()
        );

        Ingredient ingredient = ingredientRepository.findById(ingredientId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy nguyên liệu!")
        );

        double remainingNeed = ingredientNeeded;
        for (IngredientBatch batch : list) {
            if (remainingNeed <= 0) break;
            double available = batch.getQuantityRemaining();
            double take = Math.min(available, remainingNeed);
            batch.setQuantityRemaining(available - take);
            remainingNeed -= take;
            ingredientBatchRepository.save(batch);
        }
        if (remainingNeed > 0) {
            throw new IllegalArgumentException(
                    "Không đủ nguyên liệu: " + ingredient.getIngredientName() + ", thiếu: " + remainingNeed
            );
        }
    }


    @Transactional
    public void deductIngredientsByProduct(Products product, int quantity) {
        for (Recipe recipe : product.getRecipes()) {
            double totalNeed = recipe.getQuantityNeeded() * quantity;
            deductIngredient(recipe.getIngredient().getIngredientId(), totalNeed);
        }
    }

    //Hoàn lại nguyên liệu vào lô
    public void returnIngredient(Integer ingredientId, double quantityToReturn) {
        if (quantityToReturn <= 0) return;

        // Lấy danh sách batch chưa hết hạn, sắp xếp theo expiry date tăng dần (batch sắp hết hạn trước)
        List<IngredientBatch> batches = ingredientBatchRepository
                .findByIngredient_IngredientIdAndExpiryDateAfterOrderByExpiryDateAsc(
                        ingredientId, LocalDate.now()
                );

        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nguyên liệu!"));

        double remaining = quantityToReturn;

        for (IngredientBatch batch : batches) {
            if (remaining <= 0) break;


            batch.setQuantityRemaining(batch.getQuantityRemaining() + remaining);
            ingredientBatchRepository.save(batch);

            remaining = 0;
        }

        if (remaining > 0) {
            System.out.println("Cảnh báo: Hoàn " + remaining + " của nguyên liệu "
                    + ingredient.getIngredientName() + " nhưng không tìm thấy batch phù hợp.");
        }
    }

    //Hoàn lại nguyên liệu khi huỷ món

    public void returnIngredientsByProduct(Products product, int quantity) {
        if (quantity <= 0) return;

        for (Recipe recipe : product.getRecipes()) {
            double totalReturn = recipe.getQuantityNeeded() * quantity;
            returnIngredient(recipe.getIngredient().getIngredientId(), totalReturn);
        }
    }
}
