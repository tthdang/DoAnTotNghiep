package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.BatchRequest;
import com.restaurant.BeefChefBackend.dto.response.BatchResponse;
import com.restaurant.BeefChefBackend.entity.*;
import com.restaurant.BeefChefBackend.enums.BatchStatus;
import com.restaurant.BeefChefBackend.repository.IngredientBatchRepository;
import com.restaurant.BeefChefBackend.repository.IngredientRepository;
import com.restaurant.BeefChefBackend.repository.IngredientUsageRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    private IngredientUsageRepository ingredientUsageRepository;

    @Autowired
    private ProductService productService;

    private BatchResponse toResponse(IngredientBatch ingredientBatch){

//        BatchStatus status = calculateStatus(ingredientBatch);
//        ingredientBatch.setStatus(status);
//        ingredientBatchRepository.save(ingredientBatch);

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
        ingredientBatch.setStatus(calculateStatus(ingredientBatch));

        IngredientBatch save = ingredientBatchRepository.save(ingredientBatch);

        return toResponse(save);
    }

    public List<BatchResponse> getAll(){
        updateAllBatchStatus();
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

    private BatchStatus calculateStatus(IngredientBatch batch) {
        LocalDate today = LocalDate.now();

        if (batch.getQuantityRemaining() <= 0 ){
            return BatchStatus.OUT_OF_STOCK;
        }

        if (batch.getExpiryDate().isBefore(today)) {
            return BatchStatus.EXPIRED;
        }

        if (!batch.getExpiryDate().isAfter(today.plusDays(2))) {
            return BatchStatus.NEAR_EXPIRY;
        }

        return BatchStatus.AVAILABLE;
    }

    @Scheduled(cron = "0 0 0 * * ?") //mỗi ngày 00:00
    @Transactional
    public void updateBatchStatusDaily() {
        List<IngredientBatch> batches = ingredientBatchRepository.findAll();

        for (IngredientBatch batch : batches) {
            batch.setStatus(calculateStatus(batch));
        }

        ingredientBatchRepository.saveAll(batches);
    }

    @Transactional
    public void updateAllBatchStatus() {
        List<IngredientBatch> batches = ingredientBatchRepository.findAll();

        for (IngredientBatch batch : batches) {
            batch.setStatus(calculateStatus(batch));
        }

        ingredientBatchRepository.saveAll(batches);
    }


    //Trừ nguyên liệu
    @Transactional
    public void deductIngredient(Integer ingredientId, double ingredientNeeded) {
        List<IngredientBatch> list = ingredientBatchRepository.findByIngredient_IngredientIdAndQuantityRemainingGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
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

            batch.setStatus(calculateStatus(batch));
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
        productService.updateProductStatus(product);
    }

    //Hoàn lại nguyên liệu vào lô
    public void returnIngredientsByOrderItem(OrderItems orderItem) {

        List<IngredientUsage> usages =
                ingredientUsageRepository.findByOrderItem(orderItem);

        for (IngredientUsage usage : usages) {

            IngredientBatch batch = usage.getBatch();
            batch.setQuantityRemaining(batch.getQuantityRemaining() + usage.getQuantityUsed());
            batch.setStatus(calculateStatus(batch));

            ingredientBatchRepository.save(batch);
        }

        // xoá usage sau khi hoàn
        ingredientUsageRepository.deleteAll(usages);
    }

    @Transactional
    public void useIngredient(Integer ingredientId, double quantityNeeded, OrderItems orderItem) {
        if (quantityNeeded <= 0) return;

        LocalDate today = LocalDate.now();

        // Lấy các batch còn hạn sử dụng và còn số lượng > 0, sắp xếp theo hạn sử dụng gần nhất
        List<IngredientBatch> batches = ingredientBatchRepository
                .findByIngredient_IngredientIdAndQuantityRemainingGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
                        ingredientId, 0.0, today);

        double remainingNeed = quantityNeeded;

        for (IngredientBatch batch : batches) {
            if (remainingNeed <= 0) break;

            double canDeduct = Math.min(remainingNeed, batch.getQuantityRemaining());

            if (canDeduct > 0) {
                // Trừ số lượng
                batch.setQuantityRemaining(batch.getQuantityRemaining() - canDeduct);
                remainingNeed -= canDeduct;


                if (batch.getQuantityRemaining() <= 0.01) {
                    batch.setStatus(BatchStatus.OUT_OF_STOCK);
                }
                else if (batch.getQuantityRemaining() < batch.getQuantityImported() * 0.25) {
                    batch.setStatus(BatchStatus.NEAR_EXPIRY);
                }
                else {
                    batch.setStatus(BatchStatus.AVAILABLE);
                }

                ingredientBatchRepository.save(batch);
            }
        }

        // Nếu vẫn còn thiếu sau khi trừ hết các batch hợp lệ
        if (remainingNeed > 0.01) {
            throw new IllegalArgumentException(
                    String.format("Không đủ nguyên liệu cho món này. Còn thiếu %.2f", remainingNeed)
            );
        }
    }


    public List<IngredientBatch> getExpiringBatches(int days) {
        return ingredientBatchRepository
                .findExpiringSoon(LocalDate.now().plusDays(days));
    }
}
