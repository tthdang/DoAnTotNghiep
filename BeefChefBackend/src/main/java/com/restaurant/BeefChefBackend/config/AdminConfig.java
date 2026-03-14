package com.restaurant.BeefChefBackend.config;


import com.restaurant.BeefChefBackend.entity.User;
import com.restaurant.BeefChefBackend.enums.Roles;
import com.restaurant.BeefChefBackend.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Slf4j
@Configuration
public class AdminConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUserPhone("admin").isEmpty()){
                var role = new HashSet<String>();
                role.add(Roles.ADMIN.name());
                User admin = User.builder()
                        .userPhone("admin")
                        .userPassword(passwordEncoder.encode("admin"))
                        .userRole(role)
                        .build();
                userRepository.save(admin);
                log.warn("Admin user has been created with default password: admin. PLease change it!");
            }
        };
    }
}
