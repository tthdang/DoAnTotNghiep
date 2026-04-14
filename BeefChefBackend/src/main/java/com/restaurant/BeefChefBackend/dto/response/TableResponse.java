package com.restaurant.BeefChefBackend.dto.response;

import com.restaurant.BeefChefBackend.entity.Orders;
import com.restaurant.BeefChefBackend.enums.TableStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TableResponse {
    private Integer tableId;
    private String tableName;
    private int tableCapacity;
    private Integer orderId;
    private TableStatus tableStatus;
}
