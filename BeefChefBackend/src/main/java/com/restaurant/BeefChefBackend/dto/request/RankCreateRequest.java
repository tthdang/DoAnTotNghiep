package com.restaurant.BeefChefBackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RankCreateRequest {
    private String rankName;
    private Long rankMinPoint;
    private BigDecimal discount;
}
