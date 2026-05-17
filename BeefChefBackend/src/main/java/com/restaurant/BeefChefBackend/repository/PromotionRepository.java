package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Promotion;
import com.restaurant.BeefChefBackend.enums.PromotionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion,Long> {
    @Query("""
        SELECT p FROM Promotion p
        WHERE p.code = :code
          AND p.startDate <= :today
          AND p.endDate >= :today
          AND p.usedCount < p.usageLimit
          AND p.status = :status
        ORDER BY p.endDate ASC
    """)
    Optional<Promotion> findValidPromotionByCode(
            @Param("code") String code,
            @Param("today") LocalDate today,
            @Param("status") PromotionStatus status
    );
}
