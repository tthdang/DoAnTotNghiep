package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.IngredientBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IngredientBatchRepository extends JpaRepository<IngredientBatch, Integer> {
    //tìm kiếm
    List<IngredientBatch> findByIngredient_IngredientIdAndQuantityRemainingGreaterThanAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(
            Integer ingredientId,
            Double quantity,
            LocalDate today
    );


    List<IngredientBatch> findByIngredient_IngredientIdAndExpiryDateGreaterThanEqualOrderByExpiryDateAsc(Integer ingredientId, LocalDate now);

    //xử lý combo hết hạn
    @Query("""
            SELECT b FROM IngredientBatch b
            WHERE b.expiryDate <= :date
            AND b.quantityRemaining > 0
            """)
    List<IngredientBatch> findExpiringSoon(LocalDate date);
}
