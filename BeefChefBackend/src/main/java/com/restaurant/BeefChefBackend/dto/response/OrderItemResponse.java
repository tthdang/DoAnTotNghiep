package com.restaurant.BeefChefBackend.dto.response;

import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class OrderItemResponse {
    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private String productName;
    private Integer orderItemQuantity;
    private BigDecimal orderItemPrice;
    private OrderItemStatus orderItemStatus;
}
