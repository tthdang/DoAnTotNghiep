package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Products {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    private String productName;
    private BigDecimal productPrice;
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;
    private String productImage;
    private String productDescription;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;
}
