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
public class UserCreateRequest {
    private String userPhone;
    private String userPassword;
    private String userFirstname;
    private String userLastname;
    private Gender userGender;
    private LocalDate userDoB;

}
