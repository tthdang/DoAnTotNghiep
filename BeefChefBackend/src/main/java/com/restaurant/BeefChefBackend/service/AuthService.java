package com.restaurant.BeefChefBackend.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.restaurant.BeefChefBackend.dto.request.AuthRequest;
import com.restaurant.BeefChefBackend.dto.request.IntrospectRequest;
import com.restaurant.BeefChefBackend.dto.response.AuthResponse;
import com.restaurant.BeefChefBackend.dto.response.IntrospectResponse;
import com.restaurant.BeefChefBackend.entity.User;
import com.restaurant.BeefChefBackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Value("${jwt.signerKey}")
    private String signerKey;

    public AuthResponse login(AuthRequest request){
        User user = userRepository.findByUserPhone(request.getUserPhone())
                .orElseThrow(() -> new RuntimeException("Username or password not true!"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getUserPassword(), user.getUserPassword());
        if (!authenticated){
            throw new RuntimeException("Login successfully!");
        }
        var token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    //Kiểm tra xem có đúng người được phát token ko
    public IntrospectResponse introspectResponse(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();

        if(token == null || token.isBlank()){
            throw new RuntimeException("Token is missing");
        }

        JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expToken = signedJWT.getJWTClaimsSet().getExpirationTime();

        return IntrospectResponse.builder()
                .valid(signedJWT.verify(verifier) && expToken.after(new Date()))
                .build();
    }
}
