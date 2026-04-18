package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.entity.Categories;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductCreateRequest {
    private String productName;
    private BigDecimal productPrice;
    private ProductStatus productStatus;
    private String productImage;
    private String productDescription;
    private Integer productStock;
    private Integer categoryId;
    private List<RecipeRequest> recipes;
}
