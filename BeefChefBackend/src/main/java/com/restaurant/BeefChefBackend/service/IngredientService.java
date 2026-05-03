package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.IngredientCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.IngredientUpdateRequest;
import com.restaurant.BeefChefBackend.dto.response.IngredientResponse;
import com.restaurant.BeefChefBackend.entity.Ingredient;
import com.restaurant.BeefChefBackend.entity.IngredientBatch;
import com.restaurant.BeefChefBackend.repository.IngredientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    private IngredientResponse toResponse(Ingredient ingredient){
        return IngredientResponse.builder()
                .ingredientId(ingredient.getIngredientId())
                .ingredientName(ingredient.getIngredientName())
                .unit(ingredient.getUnit())
                .total(getAvailable(ingredient))
                .build();
    }

    //create
    public IngredientResponse create(IngredientCreateRequest request) {
        if (ingredientRepository.existsByIngredientName(request.getIngredientName().trim())){
            throw new IllegalArgumentException("Nguyên liệu đã tồn tại");
        }
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName(request.getIngredientName());
        ingredient.setUnit(request.getUnit());
        Ingredient save = ingredientRepository.save(ingredient);
        return toResponse(save);
    }

    //get all
    public List<IngredientResponse> getAll() {
        return ingredientRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //get by id
    public IngredientResponse getById(Integer id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () ->  new IllegalArgumentException("Không tìm thấy nguyên liệu")
        );
        return toResponse(ingredient);
    }

    //update
    public IngredientResponse update(Integer id, IngredientUpdateRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () ->  new IllegalArgumentException("Không tìm thấy nguyên liệu")
        );

        ingredient.setIngredientName(request.getIngredientName());
        ingredient.setUnit(request.getUnit());
        Ingredient save = ingredientRepository.save(ingredient);
        return toResponse(save);
    }

    //Delete
    public void delete(Integer id){
        ingredientRepository.deleteById(id);
    }



    // tính tổng số nguyên liệu dùng được
    @Transactional
    public double getAvailable(Ingredient ingredient) {
        double total = 0;
        LocalDate today = LocalDate.now();
        for (IngredientBatch batch : ingredient.getBatches()) {
            // kiểm tra xem lô nguyên liệu đã hết hạn chưa
            if (batch.getExpiryDate().isBefore(today)) {
                continue;
            }
            total += batch.getQuantityRemaining();
        }
        return total;
    }

    //xử lý combo hết hạn
    public List<Integer> getIngredientIdsFromBatches(List<IngredientBatch> batches) {
        return batches.stream()
                .map(b -> b.getIngredient().getIngredientId())
                .distinct()
                .toList();
    }
}
