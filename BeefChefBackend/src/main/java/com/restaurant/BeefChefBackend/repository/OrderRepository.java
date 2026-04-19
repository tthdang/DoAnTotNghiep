package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Orders;
import com.restaurant.BeefChefBackend.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o " +
            "WHERE o.table.tableId = :tableId " +
            "AND o.orderStatus NOT IN ('PAID', 'CANCELLED') " +
            "ORDER BY o.createdAt DESC")
    Optional<Orders> findCurrentOrderByTableId(@Param("tableId") Integer tableId);
}
