package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.UpdateOrderItemRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderItemResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orderItem")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;


    @GetMapping("/{orderItemId}")
    public ApiResponse<OrderItemResponse> getOrderItem(@PathVariable Integer orderItemId){
        OrderItems orderItem = orderItemService.getItem(orderItemId);
        try {
            return ApiResponse.<OrderItemResponse>builder()
                    .result(orderItemService.toResponse(orderItem))
                    .message("Get order item successfully!")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderItemResponse>builder()
                    .message("Lỗi khi lấy order item!" + e.getMessage())
                    .build();
        }

    }

    @PutMapping("/{orderItemId}")
    public ApiResponse<OrderItemResponse> updateOrderItemStatus(@PathVariable Integer orderItemId,
                                                                @RequestBody UpdateOrderItemRequest request){
        try{
            return ApiResponse.<OrderItemResponse>builder()
                    .result(orderItemService.updateOrderItemStatus(orderItemId, request))
                    .message("Update OrderItem successfully")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderItemResponse>builder()
                    .message("Lỗi khi update trạng thái orderItem: " + e.getMessage())
                    .build();
        }
    }
}
