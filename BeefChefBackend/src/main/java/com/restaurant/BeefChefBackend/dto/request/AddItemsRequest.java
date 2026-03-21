package com.restaurant.BeefChefBackend.dto.request;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder

public class AddItemsRequest {
    private List<OrderItemRequest> items;
}
