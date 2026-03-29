package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.AddItemsRequest;
import com.restaurant.BeefChefBackend.dto.request.OrderCreateRequest;
import com.restaurant.BeefChefBackend.dto.request.UpdateOrderRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderItemResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.entity.Orders;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import com.restaurant.BeefChefBackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;


//    private Integer orderId;
//    private Integer tableId;
//    private String tableName;
//    private LocalDateTime createdAt;
//    private Integer userId;
//    private String userName;
//    private OrderStatus orderStatus;
//    private BigDecimal orderTotal;
//    private LocalDateTime orderCreatedAt;
//    private List<OrderItemResponse> items;


    //create order
    @PostMapping()
    public ApiResponse<OrderResponse> createOrder(@RequestBody OrderCreateRequest request){

        return ApiResponse.<OrderResponse>builder()
                .result(orderService.createOrder(request.getUserId(), request.getTableId(), request.getItems()))
                .message("Order create successfully!")
                .build();
    }

    //goi them mon
    @PostMapping("/{orderId}")
    public ApiResponse<OrderResponse> addItemToOrder(@PathVariable Integer orderId, @RequestBody AddItemsRequest request){
        try{
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.addOrderItem(orderId, request.getItems()))
                    .message("Add Item to Order successfully!")
                    .build();
        } catch (Exception e){
            return ApiResponse.<OrderResponse>builder()
                    .message("Lỗi khi thêm món ăn: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Integer orderId){
        Orders orders = orderService.getOrder(orderId);
        try{
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.toResponse(orders))
                    .message("Get Order successfully!")
                    .build();
        } catch (Exception e){
            return ApiResponse.<OrderResponse>builder()
                    .message("Lỗi khi lấy được order: " + e.getMessage())
                    .build();
        }

    }

    @PutMapping("/{orderId}")
    public ApiResponse<OrderResponse> updateStatusOrder(@PathVariable Integer orderId, @RequestBody UpdateOrderRequest request){

        try{
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.updateOrderStatus(orderId, request))
                    .message("Update status Order successfully!")
                    .build();
        } catch (Exception e){
            return ApiResponse.<OrderResponse>builder()
                    .message("Lỗi khi UPDATE STATUS order: " + e.getMessage())
                    .build();
        }

    }
}
