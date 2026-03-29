package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.OrderItemRequest;
import com.restaurant.BeefChefBackend.dto.request.UpdateOrderRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderItemResponse;
import com.restaurant.BeefChefBackend.dto.response.OrderResponse;
import com.restaurant.BeefChefBackend.entity.*;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import com.restaurant.BeefChefBackend.enums.TableStatus;
import com.restaurant.BeefChefBackend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductService productService;


    public OrderResponse toResponse( Orders orders){
        List<OrderItemResponse> itemResponses = orders.getItem().stream()
                .map(item -> OrderItemResponse.builder()
                        .orderItemId(item.getOrderItemId())
                        .orderId(item.getOrder().getOrderId())
                        .productId(item.getProduct().getProductId())
                        .productName(item.getProduct().getProductName())
                        .orderItemQuantity(item.getOrderItemQuantity())
                        .orderItemPrice(item.getOrderItemPrice())
                        .orderItemStatus(item.getOrderItemStatus())
                        .orderItemCreatedAt(item.getOrderItemCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .orderId(orders.getOrderId())
                .tableId(orders.getTable().getTableId())
                .tableName(orders.getTable().getTableName())
                .createdAt(orders.getCreatedAt())
                .userId(orders.getUser().getUserId())
                .userName(orders.getUser().getUserLastname() + " " + orders.getUser().getUserFirstname())
                .orderStatus(orders.getOrderStatus())
                .orderTotal(orders.getOrderTotal())
                .orderCreatedAt(orders.getCreatedAt())
                .items(itemResponses)
                .build();
    }
//
//    //create order moi
//    public OrderResponse createOrder(OrderRequest request){
//        Tables tables = tableRepository.findById(request.getTableId()).orElseThrow(
//                () -> new RuntimeException("Table not found!")
//        );
//
//        User user = userRepository.findById(request.getUserId()).orElseThrow(
//                () -> new RuntimeException("User not found!")
//        );
//
//        Orders orders = new Orders();
//        orders.setTable(tables);
//        orders.setUser(user);
//        orders.setCreatedAt(LocalDateTime.now());
//        orders.setOrderStatus(OrderStatus.ORDERING);
//        orders.setOrderTotal(BigDecimal.ZERO);
//        Orders save = orderRepository.save(orders);
//
//        return toResponse(save);
//    }
//
//    //update order
//    public OrderResponse updateOrder(Integer orderId, AddItemsToOderRequest request){
//        Orders orders = orderRepository.findById(orderId).orElseThrow(
//                () -> new RuntimeException("Order not found!")
//        );
//
//        if(orders.getOrderStatus() == OrderStatus.PAID){
//            throw new IllegalArgumentException("Không thể thêm vì đã kết thúc!");
//        }
//
//        BigDecimal addedTotal = BigDecimal.ZERO;
//        List<OrderItemRequest> items = request.getItems();
//
//        for (int i = 0; i <items.size(); i++){
//            OrderItemRequest itemRequest = items.get(i);
//            //Check san pham
//            Products products = productRepository.findById(itemRequest.getProductId()).orElseThrow(
//                    () -> new RuntimeException("Product not found by id: " + itemRequest.getProductId())
//            );
//
//            OrderItems orderItems = new OrderItems();
//            orderItems.setOrder(orders);
//            orderItems.setProduct(products);
//            orderItems.setOrderItemQuantity(itemRequest.getQuantity());
//            orderItems.setOrderItemPrice(products.getProductPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
//            orderItems.setOrderItemStatus(OrderItemStatus.PENDING);
//
//            orders.getItem().add(orderItems);
//            addedTotal = addedTotal.add(orderItems.getOrderItemPrice());
//
//        }
//
//        //update lai tong tien
//        orders.setOrderTotal(orders.getOrderTotal().add(addedTotal));
//        Orders update = orderRepository.save(orders);
//        return toResponse(update);
//
//    }

    //create Order
    @Transactional
    public OrderResponse createOrder(Integer userId, Integer tableId, List<OrderItemRequest> lists){
        Orders order = new Orders();
        order.setOrderStatus(OrderStatus.ORDERING);
        order.setCreatedAt(LocalDateTime.now()); // set thoi gian tao order

        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User Phone: " + userId + " not found!")
        );

        Tables table = tableRepository.findById(tableId).orElseThrow(
                () -> new RuntimeException("Table not found!")
        );

        table.setTableStatus(TableStatus.OCCUPIED);//set trạng thái bàn

        List<OrderItems> orderItems = new ArrayList<>();
        //Tao total
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest orderItemRequest : lists){
            Products product = productRepository.findById(orderItemRequest.getProductId()).orElseThrow(
                    () -> new RuntimeException("Product " + orderItemRequest.getProductId() + " not found!")
            );

            //update lại stock của product đã gọi
            productService.decreaseStock(orderItemRequest.getProductId(), orderItemRequest.getQuantity());

            OrderItems item = new OrderItems();
            item.setOrder(order);
            item.setProduct(product);
            item.setOrderItemQuantity(orderItemRequest.getQuantity());
            item.setOrderItemPrice(product.getProductPrice());
            item.setOrderItemStatus(OrderItemStatus.PENDING);
            //tinh tien
            if (item.getOrderItemStatus() != OrderItemStatus.CANCEL) { //check status order item
                total = total.add(
                        product.getProductPrice().multiply(BigDecimal.valueOf(orderItemRequest.getQuantity()))
                );
            }
            //add vao list
            orderItems.add(item);
        }

        order.setTable(table);
        order.setUser(user);
        order.setItem(orderItems);
        order.setOrderTotal(total);

        Orders save = orderRepository.save(order);

        return toResponse(save);
    }

    //get order by id
    public Orders getOrder(Integer id){
        return orderRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order not found!")
        );
    }

    //add them order item moi
    //    public void addOrderItem(Integer orderId, List<OrderItemRequest> list){
//        Orders order = getOrder(orderId);
//
//        if (order.getOrderStatus() == OrderStatus.PAID) {
//            throw new IllegalStateException("Không thể thêm món: order đã thanh toán!");
//        }
//
//        BigDecimal addTotal = BigDecimal.ZERO;
//
//        for (OrderItemRequest request : list){
//            Products product = productRepository.findById(request.getProductId()).orElseThrow(
//                    () -> new RuntimeException("Product " + request.getProductId() + " not found!")
//            );
//
//            OrderItems item = new OrderItems();
//            item.setOrder(order);
//            item.setProduct(product);
//            item.setOrderItemQuantity(request.getQuantity());
//            item.setOrderItemPrice(product.getProductPrice());
//            item.setOrderItemStatus(OrderItemStatus.PENDING);
//            addTotal = addTotal.add(
//                    product.getProductPrice().multiply(BigDecimal.valueOf(request.getQuantity()))
//            );
//
//            orderItemRepository.save(item);
//            order.getItem().add(item);
//        }
//
//        order.setOrderTotal(order.getOrderTotal().add(addTotal));
//        orderRepository.save(order);
//    }

    public OrderResponse addOrderItem(Integer orderId, List<OrderItemRequest> list) {
        Orders order = getOrder(orderId);

        if (order.getOrderStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Không thể thêm món: order đã thanh toán!");
        }

        BigDecimal addTotal = BigDecimal.ZERO;

        for (OrderItemRequest request : list) {
            Products product = productRepository.findById(request.getProductId()).orElseThrow(
                    () -> new RuntimeException("Product " + request.getProductId() + " not found!")
            );

            //update lại stock của product đã gọi
            productService.decreaseStock(request.getProductId(), request.getQuantity());

            OrderItems item = new OrderItems();
            item.setOrder(order);
            item.setProduct(product);
            item.setOrderItemQuantity(request.getQuantity());
            item.setOrderItemPrice(product.getProductPrice());
            item.setOrderItemStatus(OrderItemStatus.PENDING);

            addTotal = addTotal.add(
                    product.getProductPrice().multiply(BigDecimal.valueOf(request.getQuantity()))
            );

            item.setOrderItemCreatedAt(LocalDateTime.now());

            orderItemRepository.save(item);
            order.getItem().add(item);
        }
        order.setOrderTotal(order.getOrderTotal().add(addTotal));
        Orders updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    public OrderResponse updateOrderStatus(Integer id, UpdateOrderRequest request){
        Orders order = getOrder(id);
        boolean checkStatusOrderItem = order.getItem().stream()
                .filter(i -> i.getOrderItemStatus() != OrderItemStatus.CANCEL)
                .allMatch(i -> i.getOrderItemStatus() == OrderItemStatus.SERVED);
        if(checkStatusOrderItem){
            order.setOrderStatus(request.getOrderStatus());
        }

        Orders save = orderRepository.save(order);
        return toResponse(save);

    }

}
