package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends JpaRepository<Tables, Integer> {
}
