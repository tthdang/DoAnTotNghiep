package com.restaurant.BeefChefBackend.controller;

import com.restaurant.BeefChefBackend.dto.request.OrderCreateRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderItemResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.entity.Orders;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import com.restaurant.BeefChefBackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
