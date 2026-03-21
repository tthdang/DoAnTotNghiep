package com.restaurant.BeefChefBackend.dto.request;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderCreateRequest {
    private Integer tableId;
    private Integer userId;
    private List<OrderItemRequest> items;
}
