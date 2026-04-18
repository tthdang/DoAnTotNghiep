package com.restaurant.BeefChefBackend.dto.request;


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
public class BatchRequest {
    private Integer ingredientId;
    private Double quantityImported;
    private LocalDate expiryDate;
    private BigDecimal batchPrice;
}
