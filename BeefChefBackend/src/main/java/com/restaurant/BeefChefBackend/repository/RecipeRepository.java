package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Integer> {
}
