package com.restaurant.BeefChefBackend.dto.response;

import com.restaurant.BeefChefBackend.entity.Ranks;
import com.restaurant.BeefChefBackend.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserResponse {
    private Integer userId;

    private String userPhone;
    private String userPassword;
    private String userFirstname;
    private String userLastname;
    private Gender userGender;
    private LocalDate userDoB;
    private Set<String> userRole;
    private long userPoint;

    private Ranks rank;
}
