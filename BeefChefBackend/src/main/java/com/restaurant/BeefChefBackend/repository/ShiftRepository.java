package com.restaurant.BeefChefBackend.repository;

import com.restaurant.BeefChefBackend.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Integer> {

    //Find time and date for create order
    @Query("""
    SELECT s FROM Shift s
    WHERE 
        (s.startTime <= s.endTime AND :now BETWEEN s.startTime AND s.endTime)
        OR
        (s.startTime > s.endTime AND (:now >= s.startTime OR :now <= s.endTime))
    """)
    Optional<Shift> findCurrentShift(LocalTime now);
}
