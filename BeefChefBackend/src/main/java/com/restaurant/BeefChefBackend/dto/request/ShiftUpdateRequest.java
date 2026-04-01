package com.restaurant.BeefChefBackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShiftUpdateRequest {
    private String shiftName;
    private LocalTime startTime;
    private LocalTime endTime;
}
