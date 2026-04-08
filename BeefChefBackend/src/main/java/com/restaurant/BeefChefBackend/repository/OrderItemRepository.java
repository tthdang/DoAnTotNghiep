package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.dto.response.ProductReportResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItems, Integer> {

    //Lay orderItem theo status
    List<OrderItems> findByOrderItemStatus(OrderItemStatus status);

    // thống kê theo thời gian
    @Query("""
        SELECT new com.restaurant.BeefChefBackend.dto.response.ProductReportResponse(
            p.productName,
            p.productImage,
            SUM(oi.orderItemQuantity),
            SUM(oi.orderItemQuantity * oi.orderItemPrice)
        )
        FROM OrderItems oi
        JOIN oi.product p
        JOIN oi.order o
        WHERE o.createdAt BETWEEN :start AND :end
          AND o.orderStatus = 'PAID'
        GROUP BY p.productName, p.productImage
        ORDER BY SUM(oi.orderItemQuantity) DESC
    """)
    List<ProductReportResponse> statisticByTime(LocalDateTime start, LocalDateTime end);

    //thống kê theo ca
    @Query("""
        SELECT new com.restaurant.BeefChefBackend.dto.response.ProductReportResponse(
        p.productName,
        p.productImage,
        SUM(oi.orderItemQuantity),
        SUM(oi.orderItemQuantity * oi.orderItemPrice)
        )
        FROM OrderItems oi
        JOIN oi.product p
        JOIN oi.order o
        WHERE o.shift.shiftId = :shiftId
          AND o.orderStatus = 'PAID'
        GROUP BY p.productName, p.productImage
        ORDER BY SUM(oi.orderItemQuantity) DESC
    """)
    List<ProductReportResponse> statisticByShift(Integer shiftId);
}
