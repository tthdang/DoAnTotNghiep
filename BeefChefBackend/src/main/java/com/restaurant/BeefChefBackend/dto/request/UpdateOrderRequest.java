package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateOrderRequest {
    private OrderStatus orderStatus;
}
