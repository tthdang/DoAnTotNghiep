package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.UpdateOrderItemRequest;
import com.restaurant.BeefChefBackend.dto.response.OrderItemResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.entity.Orders;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import com.restaurant.BeefChefBackend.repository.OrderItemRepository;
import com.restaurant.BeefChefBackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderItemService {
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    public OrderItemResponse toResponse(OrderItems orderItem){
        return OrderItemResponse.builder()
                .orderItemId(orderItem.getOrderItemId())
                .orderId(orderItem.getOrder().getOrderId())
                .productId(orderItem.getProduct().getProductId())
                .productName(orderItem.getProduct().getProductName())
                .orderItemQuantity(orderItem.getOrderItemQuantity())
                .orderItemPrice(orderItem.getOrderItemPrice())
                .orderItemStatus(orderItem.getOrderItemStatus())
                .orderItemCreatedAt(orderItem.getOrderItemCreatedAt())
                .build();
    }

    public OrderItems getItem(Integer id){
        return orderItemRepository.findById(id).orElseThrow(
                () -> new RuntimeException("OderItem not found!")
        );
    }


//    private boolean check(OrderItemStatus status1, OrderItemStatus status2){
//
//    }

    public OrderItemResponse updateOrderItemStatus(Integer id, UpdateOrderItemRequest request){
        OrderItems orderItem = getItem(id);

        OrderItemStatus lastStatus = orderItem.getOrderItemStatus();
        OrderItemStatus nextStatus = request.getOrderItemStatus();

        OrderItems save = new OrderItems();

        Products product = productService.getProduct(orderItem.getProduct().getProductId());


        if(nextStatus == OrderItemStatus.CANCEL){

            product.setProductStock(product.getProductStock() + orderItem.getOrderItemQuantity());

            if(product.getProductStatus() == ProductStatus.OUT_OF_STOCK){
                product.setProductStatus(ProductStatus.AVAILABLE);
            }

            Orders order = orderItem.getOrder();

            if(order == null){
                throw new RuntimeException("OrderItem không có Order!");
            }

            BigDecimal itemTotal = orderItem.getOrderItemPrice().multiply(BigDecimal.valueOf(orderItem.getOrderItemQuantity()));

            order.setOrderTotal(order.getOrderTotal().subtract(itemTotal));

            if (order.getOrderTotal().compareTo(BigDecimal.ZERO) < 0) {
                order.setOrderTotal(BigDecimal.ZERO);
            }

            orderRepository.save(order);
            orderItem.setOrderItemStatus(nextStatus);
            orderItem.setOrderItemCreatedAt(LocalDateTime.now());

            OrderItems saveCancel = orderItemRepository.save(orderItem);

            return toResponse(saveCancel);

        }

        if(lastStatus ==  OrderItemStatus.PENDING && nextStatus == OrderItemStatus.COOKING){
            orderItem.setOrderItemStatus(request.getOrderItemStatus());
            orderItem.setOrderItemCreatedAt(LocalDateTime.now());
            save = orderItemRepository.save(orderItem);

            product.setProductSold(product.getProductSold() + orderItem.getOrderItemQuantity());

            Orders orders = orderItem.getOrder();
            orders.setOrderStatus(OrderStatus.COOKING);
            orderRepository.save(orders);
        }
        if (lastStatus ==  OrderItemStatus.COOKING && nextStatus == OrderItemStatus.READY) {
            orderItem.setOrderItemStatus(request.getOrderItemStatus());
            orderItem.setOrderItemCreatedAt(LocalDateTime.now());
             save = orderItemRepository.save(orderItem);
        }
        if (lastStatus ==  OrderItemStatus.READY && nextStatus == OrderItemStatus.SERVED) {
            orderItem.setOrderItemStatus(request.getOrderItemStatus());
            orderItem.setOrderItemCreatedAt(LocalDateTime.now());
            save = orderItemRepository.save(orderItem);

        }

        return toResponse(save);
    }


}
