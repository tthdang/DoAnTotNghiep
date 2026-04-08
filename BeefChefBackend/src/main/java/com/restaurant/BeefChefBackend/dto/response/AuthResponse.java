package com.restaurant.BeefChefBackend.dto.response;


import com.restaurant.BeefChefBackend.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AuthResponse {
    private String token;
    private boolean authenticated;
    private String userName;
    private String role;
}
