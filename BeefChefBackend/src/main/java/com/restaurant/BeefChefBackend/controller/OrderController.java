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
import com.restaurant.BeefChefBackend.service.PromotionService;
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

    @Autowired
    private PromotionService promotionService;


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

        try {
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.createOrder(request.getUserPhone(), request.getTableId()))
                    .message("Tạo order thành công!")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderResponse>builder()
                    .message("Lỗi khi xếp bàn!" + e.getMessage())
                    .build();
        }
    }

    //goi them mon
    @PostMapping("/{orderId}")
    public ApiResponse<OrderResponse> addItemToOrder(@PathVariable Integer orderId, @RequestBody AddItemsRequest request){
        try{
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.addOrderItem(orderId, request.getItems()))
                    .message("Gọi món thành công!")
                    .build();
        } catch (Exception e){
            return ApiResponse.<OrderResponse>builder()
                    .message("Lỗi khi thêm món ăn: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping
    public ApiResponse<List<OrderResponse>> getOrders(){
        try {
            return ApiResponse.<List<OrderResponse>>builder()
                    .result(orderService.getOrders())
                    .message("Lấy tất cả các Order thành công")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<OrderResponse>>builder()
                    .message("Lỗi lấy cả order: " + e.getMessage())
                    .build();
        }
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getOrder(@PathVariable Integer orderId){
        Orders orders = orderService.getOrder(orderId);
        try{
            return ApiResponse.<OrderResponse>builder()
                    .result(orderService.toResponse(orders))
                    .message("Lấy order thành công!!")
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
                    .message("Cập nhật trạng thái order thành công!")
                    .build();
        } catch (Exception e){
            return ApiResponse.<OrderResponse>builder()
                    .message("Lỗi khi UPDATE STATUS order: " + e.getMessage())
                    .build();
        }

    }

    //paid order
    @PutMapping("/{orderId}/paid")
    public ApiResponse<OrderResponse> paidOrder(@PathVariable Integer orderId){
        try{
            return ApiResponse.<OrderResponse>builder()
                    .message("Thanh toán thành công!")
                    .result(orderService.paidOrder(orderId))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderResponse>builder()
                    .message("Thanh toán thất bại!" + e.getMessage())
                    .build();
        }
    }

    //cancel OrderItem
    @PutMapping("/{orderId}/{orderItemId}/cancel")
    public ApiResponse<OrderResponse> cancelItem(@PathVariable Integer orderId, @PathVariable Integer orderItemId) {
        try {
            return ApiResponse.<OrderResponse>builder()
                    .message("Huỷ món ăn thành công!")
                    .result(orderService.cancelOrderItem(orderId, orderItemId))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderResponse>builder()
                    .message("Lỗi khi huỷ món ăn: " + e.getMessage())
                    .build();
        }
    }

    //áp dụng khuyến mãi
    @PostMapping("/{orderId}/applyPromotion")
    public ApiResponse<OrderResponse> applyPromotion(@PathVariable Integer orderId, @RequestParam String code) {

        try{
            promotionService.applyPromotion(orderId, code);
            Orders order = orderService.getOrder(orderId);
            return ApiResponse.<OrderResponse>builder()
                    .message("Áp dụng mã khuyến mãi thành công!")
                    .result(orderService.toResponse(order))
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderResponse>builder()
                    .message("Áp dụng mã khuyến mãi không thành công: " + e.getMessage())
                    .build();
        }
    }
}
