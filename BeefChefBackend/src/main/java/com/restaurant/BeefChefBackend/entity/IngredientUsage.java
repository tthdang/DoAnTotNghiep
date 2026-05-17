package com.restaurant.BeefChefBackend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class IngredientUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "orderItemId")
    private OrderItems orderItem;

    @ManyToOne
    @JoinColumn(name = "batchId")
    private IngredientBatch batch;

    private double quantityUsed;
}
