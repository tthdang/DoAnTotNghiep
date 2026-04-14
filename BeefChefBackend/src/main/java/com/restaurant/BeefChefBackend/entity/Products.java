package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

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
    private Integer productSold;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categories category;

    @Transient
    private Integer productStock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Recipe> recipes;
}
