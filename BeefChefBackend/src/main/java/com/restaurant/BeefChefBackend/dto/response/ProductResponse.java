package com.restaurant.BeefChefBackend.dto.response;

import com.restaurant.BeefChefBackend.enums.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProductResponse {
    private Integer productId;
    private String productName;
    private BigDecimal productPrice;
    private ProductStatus productStatus;
    private String productImage;
    private String productDescription;
    private Integer productStock;
    private Integer productSold;
    private String categoryName;
}
