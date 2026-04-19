package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Promotion;
import com.restaurant.BeefChefBackend.entity.PromotionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionItemRepository extends JpaRepository<PromotionItem, Long> {
    List<PromotionItem> findAllByPromotion(Promotion promotion);
    //dn 1 mon ăn
    Optional<PromotionItem> findByPromotion(Promotion promotion);
}
