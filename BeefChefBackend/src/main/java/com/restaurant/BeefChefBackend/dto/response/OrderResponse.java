package com.restaurant.BeefChefBackend.dto.response;

import com.restaurant.BeefChefBackend.entity.Shift;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderResponse {
    private Integer orderId;
    private Integer tableId;
    private String tableName;
    private LocalDateTime createdAt;
    private Integer userId;
    private String userName;
    private String userRank;
    private OrderStatus orderStatus;
    private BigDecimal orderTotal;
    private LocalDateTime orderCreatedAt;
    private Shift shift;
    private BigDecimal userRankDiscount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
    private String promotionCode;
    private LocalDateTime paidAt;
    private List<OrderItemResponse> items;

}
