package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.TableStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TableCreateRequest {
    private String tableName;
    private int tableCapacity;
    private TableStatus tableStatus;
}
