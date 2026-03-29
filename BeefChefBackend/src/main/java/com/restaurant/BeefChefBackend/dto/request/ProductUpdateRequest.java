package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.ProductStatus;
import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductUpdateRequest {
    private String productName;
    private BigDecimal productPrice;
    private ProductStatus productStatus;
    private String productImage;
    private String productDescription;
    private Integer productStock;
    private Integer categoryId;
}
