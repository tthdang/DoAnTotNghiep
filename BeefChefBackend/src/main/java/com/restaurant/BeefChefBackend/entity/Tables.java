package com.restaurant.BeefChefBackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.restaurant.BeefChefBackend.enums.TableStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Orders> orders;
}
