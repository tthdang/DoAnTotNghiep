package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.UpdateOrderItemRequest;
import com.restaurant.BeefChefBackend.dto.response.OrderItemResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.entity.Orders;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.entity.Tables;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import com.restaurant.BeefChefBackend.repository.OrderItemRepository;
import com.restaurant.BeefChefBackend.repository.OrderRepository;
import com.restaurant.BeefChefBackend.repository.ProductRepository;
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
    private ProductRepository productRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private IngredientBatchService ingredientBatchService;

    public OrderItemResponse toResponse(OrderItems orderItem){
        Orders order = orderItem.getOrder();

        Tables table = order.getTable();

        return OrderItemResponse.builder()
                .orderItemId(orderItem.getOrderItemId())
                .orderId(orderItem.getOrder().getOrderId())
                .productId(orderItem.getProduct().getProductId())
                .productName(orderItem.getProduct().getProductName())
                .orderItemQuantity(orderItem.getOrderItemQuantity())
                .orderItemPrice(orderItem.getOrderItemPrice())
                .orderItemStatus(orderItem.getOrderItemStatus())
                .tableName(table.getTableName())
                .orderItemCreatedAt(orderItem.getOrderItemCreatedAt())
                .build();
    }

    public OrderItems getItem(Integer id){
        return orderItemRepository.findById(id).orElseThrow(
                () -> new RuntimeException("OderItem Không tìm thấy!")
        );
    }

    //get OrderItem theo status
    public List<OrderItemResponse> getOrderItemByStatus(OrderItemStatus orderItemStatus){
        return orderItemRepository.findByOrderItemStatus(orderItemStatus)
                .stream()
                .map(this::toResponse)
                .toList();
    }


//    private boolean check(OrderItemStatus status1, OrderItemStatus status2){
//
//    }

    //update trạng thai orderItem
    public OrderItemResponse updateOrderItemStatus(Integer id, UpdateOrderItemRequest request){
        OrderItems orderItem = getItem(id);

        OrderItemStatus lastStatus = orderItem.getOrderItemStatus();
        OrderItemStatus nextStatus = request.getOrderItemStatus();

        OrderItems save = new OrderItems();

        Products product = orderItem.getProduct();


        if(lastStatus ==  OrderItemStatus.PENDING && nextStatus == OrderItemStatus.COOKING){

//            ingredientBatchService.deductIngredientsByProduct(product, orderItem.getOrderItemQuantity());

            orderItem.setOrderItemStatus(request.getOrderItemStatus());

            save = orderItemRepository.save(orderItem);

            product.setProductSold(product.getProductSold() + orderItem.getOrderItemQuantity());

            Orders orders = orderItem.getOrder();
            orders.setOrderStatus(OrderStatus.COOKING);
            orderRepository.save(orders);
        }

        if (lastStatus ==  OrderItemStatus.COOKING && nextStatus == OrderItemStatus.READY) {
            orderItem.setOrderItemStatus(request.getOrderItemStatus());

             save = orderItemRepository.save(orderItem);
        }
        if (lastStatus ==  OrderItemStatus.READY && nextStatus == OrderItemStatus.SERVED) {
            orderItem.setOrderItemStatus(request.getOrderItemStatus());

            save = orderItemRepository.save(orderItem);

            orderService.autoUpdateOrderStatus(orderItem.getOrder().getOrderId());

        }

        return toResponse(save);
    }


}
