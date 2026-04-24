package com.restaurant.BeefChefBackend.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationRequest {
    private String customerName;
    private String customerPhone;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    private int numberOfPeople;
    private String note;
    private Integer tableId;
}
