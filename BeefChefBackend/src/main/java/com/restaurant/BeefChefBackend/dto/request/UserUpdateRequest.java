package com.restaurant.BeefChefBackend.dto.request;

import com.restaurant.BeefChefBackend.enums.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UserUpdateRequest {
    private String userPassword;
    private String userFirstName;
    private String userLastname;
    private Gender userGender;
    private LocalDate userDoB;
}
