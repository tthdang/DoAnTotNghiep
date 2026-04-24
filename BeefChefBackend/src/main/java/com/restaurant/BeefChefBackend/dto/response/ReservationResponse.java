package com.restaurant.BeefChefBackend.dto.response;

import com.restaurant.BeefChefBackend.entity.Tables;
import com.restaurant.BeefChefBackend.enums.ReservationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationResponse {
    private Integer reservationId;
    private String customerName;
    private String customerPhone;

    private LocalDateTime date;

    private int numberOfPeople;
    private String note;

    private String status;

    private Integer tableId;
    private String tableName;
    private int tableCapacity;
}
