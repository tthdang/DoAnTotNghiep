package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.IngredientUsage;
import com.restaurant.BeefChefBackend.entity.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IngredientUsageRepository extends JpaRepository<IngredientUsage, Integer> {
    List<IngredientUsage> findByOrderItem(OrderItems items);
}
