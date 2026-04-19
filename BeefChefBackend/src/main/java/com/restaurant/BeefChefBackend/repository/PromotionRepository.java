package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {
    Optional<Promotion> findByCode(String code);
}
