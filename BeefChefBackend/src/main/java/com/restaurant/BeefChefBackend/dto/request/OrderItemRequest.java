package com.restaurant.BeefChefBackend.dto.request;


import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemRequest {
    private Integer productId;
    private Integer quantity = 1;
}
