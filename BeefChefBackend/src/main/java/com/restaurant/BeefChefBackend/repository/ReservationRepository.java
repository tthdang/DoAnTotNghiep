package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Reservation;
import com.restaurant.BeefChefBackend.entity.Tables;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByTablesAndDateBetween(
            Tables tables,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Reservation> findByDateBetween(LocalDateTime start, LocalDateTime end);

}
