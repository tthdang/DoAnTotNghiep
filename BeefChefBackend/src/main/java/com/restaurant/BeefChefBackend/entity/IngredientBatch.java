package com.restaurant.BeefChefBackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class IngredientBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ingredientBatchId;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private Double ingredientBatchQuantityImported;
    private Double ingredientBatchQuantityRemaining;
    private LocalDate ingredientBatchStartDate;
    private LocalDate ingredientBatchExpiryDate;
    private String ingredientBatchStorageType;
}
