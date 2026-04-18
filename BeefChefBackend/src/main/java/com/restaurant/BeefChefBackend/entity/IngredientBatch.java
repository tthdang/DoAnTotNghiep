package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.BatchStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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
    private Integer batchId;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
    private Double quantityImported; //sl nhập
    private Double quantityRemaining; //sl còn
    private LocalDate importDate;
    private LocalDate expiryDate;
    private BigDecimal batchPrice;

    @Enumerated(EnumType.STRING)
    private BatchStatus status = BatchStatus.AVAILABLE;

    public boolean isNearExpiry() {
        return expiryDate.isBefore(LocalDate.now().plusDays(4));
    }
}
