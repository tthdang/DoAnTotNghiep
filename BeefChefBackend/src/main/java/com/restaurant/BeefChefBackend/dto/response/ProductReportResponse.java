package com.restaurant.BeefChefBackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductReportResponse {
    private String productName;
    private String productImage;
    private Long quantity;
    private BigDecimal totalAmount;
}
