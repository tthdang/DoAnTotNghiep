package com.restaurant.BeefChefBackend.entity;

import com.restaurant.BeefChefBackend.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String userPhone;
    private String userPassword;
    private String userFirstname;
    private String userLastname;
    @Enumerated(EnumType.STRING)
    private Gender userGender;
    private LocalDate userDoB;
    private Set<String> userRole;
    private long userPoint;
}
