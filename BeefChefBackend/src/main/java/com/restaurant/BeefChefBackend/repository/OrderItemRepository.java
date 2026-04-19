package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.dto.response.ProductReportResponse;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import com.restaurant.BeefChefBackend.enums.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItems, Integer> {

    //Lay orderItem theo status
    List<OrderItems> findByOrderItemStatus(OrderItemStatus status);

    // thống kê theo ngày trong tháng
    @Query("""
        SELECT DAY(o.createdAt), SUM(o.orderTotal)
        FROM Orders o
        WHERE MONTH(o.createdAt) = :month
            AND YEAR(o.createdAt) = :year
            AND o.orderStatus = 'PAID'
        GROUP BY DAY(o.createdAt)
        ORDER BY DAY(o.createdAt)
    """)
    List<Object[]> reportByDay(int month, int year);

    //thống kê tuần theo tháng
    @Query("""
        SELECT WEEK(o.createdAt), SUM(o.orderTotal)
        FROM Orders o
        WHERE MONTH(o.createdAt) = :month
            AND YEAR(o.createdAt) = :year
            AND o.orderStatus = 'PAID'
        GROUP BY WEEK(o.createdAt)
        ORDER BY WEEK(o.createdAt)
    """)
    List<Object[]> reportByWeek(int month, int year);

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
            AND oi.orderItemStatus = 'SERVED'
            AND o.createdAt BETWEEN :start AND :end
        GROUP BY p.productName, p.productImage
        ORDER BY SUM(oi.orderItemQuantity) DESC
    """)
    List<ProductReportResponse> statisticByShiftAndDate(
            Integer shiftId,
            LocalDateTime start,
            LocalDateTime end
    );

    //đếm số đơn
    @Query("""
        SELECT COUNT(DISTINCT o.orderId)
        FROM OrderItems oi
        JOIN oi.order o
        WHERE o.shift.shiftId = :shiftId
            AND o.orderStatus = 'PAID'
            AND o.createdAt BETWEEN :start AND :end
    """)
    int countOrdersByShiftAndDate(
            Integer shiftId,
            LocalDateTime start,
            LocalDateTime end
    );
}
