package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<Tables, Integer> {
}
