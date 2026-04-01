package com.restaurant.BeefChefBackend.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.BeefChefBackend.entity.User;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RankResponse {
    private Integer rankId;
    private String rankName;
    private Long rankMinPoint;
    
}
