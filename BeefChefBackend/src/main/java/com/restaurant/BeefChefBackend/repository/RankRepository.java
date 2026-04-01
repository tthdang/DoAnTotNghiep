package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Ranks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankRepository extends JpaRepository<Ranks, Integer> {
    Optional<Ranks> findByRankMinPoint(Long minPoint);
}
