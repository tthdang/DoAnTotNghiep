package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reservationId;

    private String customerName;
    private String customerPhone;
    private LocalDateTime date;
    private String note;

    private int numberOfPeople;

    @ManyToOne
    @JoinColumn(name = "tableId")
    private Tables tables;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;



}
