package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.DiscountType;
import com.restaurant.BeefChefBackend.enums.PromotionStatus;
import com.restaurant.BeefChefBackend.enums.PromotionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long promotionId;

    private String code;
    @Enumerated(EnumType.STRING)
    private PromotionType promotionType;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountValue;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private PromotionStatus status;

    private Integer usageLimit;
    private Integer usedCount;

}
