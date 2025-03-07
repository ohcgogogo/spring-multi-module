package com.example.appbatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(i -> i.disable())
                .cors(i -> i.disable())
                .headers(i ->
                        i.frameOptions(frameOptions -> frameOptions.sameOrigin())
                                .disable()
                )
                .authorizeHttpRequests(
                        authorize -> authorize.anyRequest().permitAll()
                );
        return http.build();
    }
}
