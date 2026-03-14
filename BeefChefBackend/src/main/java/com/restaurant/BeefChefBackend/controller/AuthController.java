package com.restaurant.BeefChefBackend.controller;

import com.nimbusds.jose.JOSEException;
import com.restaurant.BeefChefBackend.dto.request.AuthRequest;
import com.restaurant.BeefChefBackend.dto.request.IntrospectRequest;
import com.restaurant.BeefChefBackend.dto.response.ApiResponse;
import com.restaurant.BeefChefBackend.dto.response.AuthResponse;
import com.restaurant.BeefChefBackend.dto.response.IntrospectResponse;
import com.restaurant.BeefChefBackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService service;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login (@RequestBody AuthRequest request){
        var result = service.login(request);
        return ApiResponse.<AuthResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var result = service.introspectResponse(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();
    }
}
