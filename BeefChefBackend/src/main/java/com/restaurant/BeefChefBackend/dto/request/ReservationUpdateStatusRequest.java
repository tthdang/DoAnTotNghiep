package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.ReservationStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReservationUpdateStatusRequest {
    private ReservationStatus status;
}
