package com.restaurant.BeefChefBackend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Ranks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rankId;
    private String rankName;
    private Long rankMinPoint;
    @OneToMany(mappedBy = "rank")
    @JsonIgnore
    private Set<User> userId;
}
