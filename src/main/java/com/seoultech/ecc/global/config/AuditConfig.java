package com.seoultech.ecc.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfig {

    // 시스템 처리를 나타내는 상수
    private static final Integer SYSTEM_USER_ID = -1;

    @Bean
    public AuditorAware<Integer> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() ||
                    authentication.getPrincipal().equals("anonymousUser")) {
                // 시스템 처리인 경우 상수값 반환 (null 대신)
                return Optional.of(SYSTEM_USER_ID);
            }

            // Authentication의 credentials에서 uuid 가져오기
            if (authentication.getCredentials() instanceof Integer) {
                return Optional.of((Integer) authentication.getCredentials());
            }

            // 예외 상황에도 기본값 반환
            return Optional.of(SYSTEM_USER_ID);
        };
    }
}