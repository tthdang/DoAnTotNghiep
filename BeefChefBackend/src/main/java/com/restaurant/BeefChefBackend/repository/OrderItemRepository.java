package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItems, Integer> {
}
