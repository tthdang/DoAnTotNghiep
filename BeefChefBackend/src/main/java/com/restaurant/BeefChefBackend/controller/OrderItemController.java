package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.UpdateOrderItemRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderItemResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import com.restaurant.BeefChefBackend.service.OrderItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ApiResponse<List<OrderItemResponse>> getByStatus(@RequestParam OrderItemStatus status) {
        try {
            return ApiResponse.<List<OrderItemResponse>>builder()
                    .message("Lấy toàn bộ orderItem theo " + status)
                    .result(orderItemService.getOrderItemByStatus(status))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<OrderItemResponse>>builder()
                    .message("Lỗi lấy toàn bộ orderItem theo " + status)
                    .build();
        }
    }

    @PutMapping("/{orderItemId}")
    @Transactional
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
