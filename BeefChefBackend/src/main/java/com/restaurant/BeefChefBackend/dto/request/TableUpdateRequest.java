package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TableUpdateRequest {
    private String tableName;
    private int tableCapacity;
    private TableStatus tableStatus;
}
