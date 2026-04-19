package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.DiscountType;
import com.restaurant.BeefChefBackend.enums.PromotionType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class PromotionCreateRequest {
    private String code;
    private PromotionType promotionType;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usageLimit;
}
