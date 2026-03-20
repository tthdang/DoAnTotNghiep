package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.entity.Categories;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

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
    private Integer categoryId;
}
