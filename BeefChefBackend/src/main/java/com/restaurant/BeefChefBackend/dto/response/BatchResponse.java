package com.restaurant.BeefChefBackend.dto.response;

import com.restaurant.BeefChefBackend.entity.Ingredient;
import com.restaurant.BeefChefBackend.enums.BatchStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BatchResponse {
    private Integer batchId;
    private Integer ingredientId;
    private String ingredientName;
    private Double quantityImported;
    private Double quantityRemaining;
    private LocalDate importDate;
    private LocalDate expiryDate;
    private BigDecimal batchPrice;
    private BatchStatus status;
}
