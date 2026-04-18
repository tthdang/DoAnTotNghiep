package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
    //Ktra ngliệu
    boolean existsByIngredientName(String ingredientName);
}
