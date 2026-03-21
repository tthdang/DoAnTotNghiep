package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.TableStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Tables {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tableId;
    private String tableName;
    private int tableCapacity;

    @Enumerated(EnumType.STRING)
    private TableStatus tableStatus;
}
