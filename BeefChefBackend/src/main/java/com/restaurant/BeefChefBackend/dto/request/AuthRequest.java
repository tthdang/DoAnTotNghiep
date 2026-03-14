package com.restaurant.BeefChefBackend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AuthRequest {
    private String userPhone;
    private String userPassword;
}
