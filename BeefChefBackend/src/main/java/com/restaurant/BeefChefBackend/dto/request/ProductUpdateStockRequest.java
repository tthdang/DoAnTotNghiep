package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.ProductStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Data
public class ProductUpdateStockRequest {
    private Integer productStock;
    private ProductStatus productStatus;
}
