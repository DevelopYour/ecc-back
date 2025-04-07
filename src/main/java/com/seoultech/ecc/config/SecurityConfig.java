package com.seoultech.ecc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("""
                ROLE_OWNER > ROLE_MANAGER
                ROLE_MANAGER > ROLE_USER
                """);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인가
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/login", "/join").permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("MANAGER")
                        .anyRequest().authenticated()
                );

        // csrf
        http.csrf((auth) -> auth.disable());
        return http.build();
    }
}
