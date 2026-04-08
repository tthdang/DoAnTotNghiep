package com.restaurant.BeefChefBackend.config;


import com.restaurant.BeefChefBackend.entity.Ranks;
import com.restaurant.BeefChefBackend.entity.User;
import com.restaurant.BeefChefBackend.enums.Roles;
import com.restaurant.BeefChefBackend.repository.UserRepository;
import com.restaurant.BeefChefBackend.service.RankService;
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

    @Autowired
    private RankService rankService;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return args -> {
            if(userRepository.findByUserPhone("admin").isEmpty()){
                var role = new HashSet<String>();
                role.add(Roles.ADMIN.name());
                Ranks rank = rankService.getRankById(5);
                User admin = User.builder()
                        .userPhone("admin")
                        .userPassword(passwordEncoder.encode("admin"))
                        .userRole(role)
                        .userPoint(100000L)
                        .rank(rank)
                        .build();
                userRepository.save(admin);
                log.warn("Admin user has been created with default password: admin. PLease change it!");
            }
            if(userRepository.findByUserPhone("chef123").isEmpty()){
                var role = new HashSet<String>();
                role.add(Roles.CHEF.name());
                Ranks rank = rankService.getRankById(5);
                User admin = User.builder()
                        .userPhone("chef123")
                        .userPassword(passwordEncoder.encode("chef123"))
                        .userRole(role)
                        .userPoint(100000L)
                        .rank(rank)
                        .build();
                userRepository.save(admin);
                log.warn("Chef user has been created with default password: chef. PLease change it!");
            }
        };
    }
}
