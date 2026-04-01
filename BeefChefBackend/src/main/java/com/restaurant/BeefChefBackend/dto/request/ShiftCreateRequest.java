package com.restaurant.BeefChefBackend.dto.request;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftCreateRequest {
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
}
