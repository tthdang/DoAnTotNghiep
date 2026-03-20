package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Products, Integer> {
    Optional<Products> findByProductId(Integer productId);
}
