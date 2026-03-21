package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders, Integer> {
}
