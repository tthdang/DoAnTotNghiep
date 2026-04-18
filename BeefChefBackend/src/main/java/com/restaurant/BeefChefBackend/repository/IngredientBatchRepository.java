package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.IngredientBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface IngredientBatchRepository extends JpaRepository<IngredientBatch, Integer> {
    //tìm kiếm
    List<IngredientBatch> findByIngredient_IngredientIdAndQuantityRemainingGreaterThanAndExpiryDateAfterOrderByExpiryDateAsc(
            Integer ingredientId,
            Double quantity,
            LocalDate today
    );

    List<IngredientBatch> findByIngredient_IngredientIdAndExpiryDateAfterOrderByExpiryDateAsc(Integer ingredientId, LocalDate now);
}
