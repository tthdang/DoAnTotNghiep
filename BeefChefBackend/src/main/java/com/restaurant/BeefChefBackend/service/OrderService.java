package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.OrderItemRequest;
import com.restaurant.BeefChefBackend.dto.request.UpdateOrderRequest;
import com.restaurant.BeefChefBackend.dto.response.*;
import com.restaurant.BeefChefBackend.entity.*;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import com.restaurant.BeefChefBackend.enums.OrderStatus;
import com.restaurant.BeefChefBackend.enums.ProductStatus;
import com.restaurant.BeefChefBackend.enums.TableStatus;
import com.restaurant.BeefChefBackend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private UserService userService;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private IngredientBatchService ingredientBatchService;

    @Autowired
    private PromotionService promotionService;

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
                .userRank(orders.getUser().getRank().getRankName())
                .userRankDiscount(orders.getUserRankDiscount())
                .orderStatus(orders.getOrderStatus())
                .orderTotal(orders.getOrderTotal())
                .orderCreatedAt(orders.getCreatedAt())
                .items(itemResponses)
                .shift(orders.getShift())
                .paidAt(orders.getPaidAt())
                .discountAmount(orders.getDiscountAmount())
                .finalAmount(orders.getFinalAmount())
                .promotionCode(orders.getPromotion() != null ? orders.getPromotion().getCode() : null)
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
    public OrderResponse createOrder(String userPhone, Integer tableId){
        Orders order = new Orders();
        order.setOrderStatus(OrderStatus.ORDERING);
        order.setCreatedAt(LocalDateTime.now()); // set thoi gian tao order

        Shift shift = shiftService.getCurrentShift();
        order.setShift(shift);

        User user = userRepository.findByUserPhone(userPhone).orElseThrow(
                () -> new RuntimeException("User Phone: " + userPhone + " not found!")
        );

        Tables table = tableRepository.findById(tableId).orElseThrow(
                () -> new RuntimeException("Table not found!")
        );


        if(table.getTableStatus() == TableStatus.OCCUPIED){
            throw  new IllegalStateException("Bàn đã được sử dụng hoặc đặt không thể xếp được");
        }

        table.setTableStatus(TableStatus.OCCUPIED);//set trạng thái bàn

        List<OrderItems> orderItems = new ArrayList<>();

        order.setTable(table);
        order.setUser(user);
        order.setItem(orderItems);
        order.setOrderTotal(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFinalAmount(BigDecimal.ZERO);


        Orders save = orderRepository.save(order);

        return toResponse(save);
    }

    //get order by id
    public Orders getOrder(Integer id){
        return orderRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("Order not found!")
        );
    }

    //getAllOrder
    public List<OrderResponse> getOrders(){
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    //add them order item moi
    @Transactional
    public OrderResponse addOrderItem(Integer orderId, List<OrderItemRequest> list) {
        Orders order = getOrder(orderId);

        if (order.getOrderStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Không thể thêm món: order đã thanh toán!");
        }

        BigDecimal addTotal = BigDecimal.ZERO;

        for (OrderItemRequest request : list) {
            Products product = productRepository.findById(request.getProductId()).orElseThrow(
                    () -> new RuntimeException("Không tìm thấy sản phẩm id: " + request.getProductId())
            );

            // Kiểm tra stock từ nguyên liệu
            int maxCanMake = productService.calculateStock(product);
            if (request.getQuantity() > maxCanMake) {
                throw new IllegalArgumentException(
                        String.format("Món '%s' hiện chỉ làm được tối đa %d phần. Bạn gọi %d phần!",
                                product.getProductName(), maxCanMake, request.getQuantity())
                );
            }

            // Tạo OrderItem
            OrderItems item = new OrderItems();
            item.setOrder(order);
            item.setProduct(product);
            item.setOrderItemQuantity(request.getQuantity());
            item.setOrderItemPrice(product.getProductPrice());
            item.setOrderItemStatus(OrderItemStatus.PENDING);
            item.setOrderItemCreatedAt(LocalDateTime.now());

            // lưu lại orderItem
            OrderItems savedItem = orderItemRepository.save(item);
            order.getItem().add(savedItem);

            BigDecimal itemTotal = product.getProductPrice()
                    .multiply(BigDecimal.valueOf(request.getQuantity()));
            addTotal = addTotal.add(itemTotal);

            //trừ nguyên liệu
            try {
                for (Recipe recipe : product.getRecipes()) {
                    double totalNeed = recipe.getQuantityNeeded() * request.getQuantity();

                    ingredientBatchService.useIngredient(
                            recipe.getIngredient().getIngredientId(),
                            totalNeed,
                            savedItem
                    );
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Lỗi khi trừ nguyên liệu cho món "
                        + product.getProductName() + ": " + e.getMessage());
            }
        }

        // Cập nhật tổng tiền order
        order.setOrderTotal(order.getOrderTotal().add(addTotal));
        order.setOrderStatus(OrderStatus.ORDERING);

        Orders updatedOrder = orderRepository.save(order);

        return toResponse(updatedOrder);
    }

    //update trạng thái order
    public OrderResponse updateOrderStatus(Integer id, UpdateOrderRequest request){
        Orders order = getOrder(id);
        boolean checkStatusOrderItem = order.getItem().stream()
                .filter(i -> i.getOrderItemStatus() != OrderItemStatus.CANCEL)
                .allMatch(i -> i.getOrderItemStatus() == OrderItemStatus.SERVED);
        if(checkStatusOrderItem){
            order.setOrderStatus(request.getOrderStatus());
        }else{
            throw new IllegalArgumentException("Tất cả các orderItem liên quan chưa được mang ra bàn!");
        }

        Orders save = orderRepository.save(order);
        return toResponse(save);

    }

    //cancel orderItem
    public OrderResponse cancelOrderItem(Integer orderId, Integer orderItemId){
        OrderItems orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy OrderItem có id: " + orderItemId)
        );

        if(!orderItem.getOrder().getOrderId().equals(orderId)){
            throw new IllegalArgumentException("Món ăn này không thuộc order có id: " + orderId);
        }

        if (orderItem.getOrderItemStatus() != OrderItemStatus.PENDING) {
            throw new IllegalStateException("Món đã được nấu không thể huỷ!");
        }

        Products product = orderItem.getProduct();

        // Hoàn lại stock
        if (product != null && orderItem.getOrderItemQuantity() > 0) {
            // Hoàn nguyên liệu
            ingredientBatchService.returnIngredientsByOrderItem(orderItem);
            productRepository.save(product);
        }

        // Cập nhật trạng thái hủy
        orderItem.setOrderItemStatus(OrderItemStatus.CANCEL);
        orderItem.setOrderItemCreatedAt(LocalDateTime.now());
        orderItemRepository.save(orderItem);

        // Cập nhật tổng tiền order
        Orders order = orderItem.getOrder();
        BigDecimal itemTotal = orderItem.getOrderItemPrice()
                .multiply(BigDecimal.valueOf(orderItem.getOrderItemQuantity()));

        order.setOrderTotal(order.getOrderTotal().subtract(itemTotal));
        if (order.getOrderTotal().compareTo(BigDecimal.ZERO) < 0) {
            order.setOrderTotal(BigDecimal.ZERO);
        }

        Orders save = orderRepository.save(order);

        return toResponse(save);
    }


    //Pay order
    public OrderResponse paidOrder(Integer id){
        Orders order = getOrder(id);
        if(order.getOrderStatus() == OrderStatus.PAID){
            throw new IllegalStateException("Hoá đơn đã được thanh toán rồi!");
        }

        if(order.getOrderStatus() != OrderStatus.SERVED){
            throw new IllegalStateException("Order chưa được hoàn thành tât cả các món thì không được thanh toán!");
        }

        BigDecimal finalAmount = order.getFinalAmount();

        if (finalAmount == null) {
            // Trường hợp chưa áp dụng promotion nào
            BigDecimal rankDiscount = promotionService.calculateRankDiscount(order);
            finalAmount = order.getOrderTotal()
                    .subtract(rankDiscount)
                    .max(BigDecimal.ZERO);

            order.setUserRankDiscount(rankDiscount);
            order.setDiscountAmount(BigDecimal.ZERO);
            order.setFinalAmount(finalAmount);
        }

        order.setOrderStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        Orders saved = orderRepository.save(order);


        //cong diem va cap nhat lai diem/rank cho user
        User user = saved.getUser();
        if (user != null && !"0968425402".equals(user.getUserPhone())) {
            int earnedPoint = finalAmount
                    .divide(BigDecimal.valueOf(10000), 0, RoundingMode.DOWN)
                    .intValue();

            user.setUserPoint(user.getUserPoint() + earnedPoint);
            userService.updateRankForUser(user);
            userRepository.save(user);
        }

        //cập nhật lai trạng thái bàn
        Tables table = saved.getTable();
        if (table != null) {
            table.setTableStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        return toResponse(saved);
    }

    // Lấy order theo id bàn
    public Orders getCurrentOrderByTableId(Integer tableId) {
        return orderRepository.findCurrentOrderByTableId(tableId).orElse(null);
    }

    //tu dong cap nhat trang thai order
    public void autoUpdateOrderStatus(Integer orderId){
        Orders order = getOrder(orderId);

        //bỏ qua cancel
        boolean allItemsServed = order.getItem().stream()
                .filter(item -> item.getOrderItemStatus() != OrderItemStatus.CANCEL)
                .allMatch(item -> item.getOrderItemStatus() == OrderItemStatus.SERVED);


        if (allItemsServed && order.getOrderStatus() != OrderStatus.SERVED) {
            order.setOrderStatus(OrderStatus.SERVED);
            orderRepository.save(order);

        }
    }


}
