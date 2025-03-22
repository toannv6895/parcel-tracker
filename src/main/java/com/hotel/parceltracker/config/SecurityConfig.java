package com.hotel.parceltracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .requestMatchers("/api/guests/**", "/api/parcels/**").authenticated()
                .requestMatchers("/h2-console/**", "/swagger-ui/**").permitAll()
                .and()
                .httpBasic()
                .and()
                .csrf().disable()
                .headers()
                .frameOptions()
                .disable();
        return http.build();
    }
}