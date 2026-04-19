package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItems {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderItemId;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private Orders order;

    @ManyToOne
    @JoinColumn(name = "productId")
    private Products product;

    private Integer orderItemQuantity;
    private BigDecimal orderItemPrice;

    @Enumerated(EnumType.STRING)
    private OrderItemStatus orderItemStatus;

    private LocalDateTime orderItemCreatedAt;

    private BigDecimal discountAmount;
    private BigDecimal finalPrice;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

}
