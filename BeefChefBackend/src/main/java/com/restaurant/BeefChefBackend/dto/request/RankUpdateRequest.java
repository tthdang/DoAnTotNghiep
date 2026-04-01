package com.restaurant.BeefChefBackend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RankUpdateRequest {
    private String rankName;
    private Long rankMinPoint;
}
