package com.seoultech.ecc.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Todo: JWT 적용 후 주석 제거

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String jwt = "Bearer Authentication";

        // 1. Security Requirement (global security setting)
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

        // 2. Security Scheme 정의
        SecurityScheme securityScheme = new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        // 3. Components에 security scheme 추가
        Components components = new Components()
                .addSecuritySchemes(jwt, securityScheme);

        // 4. OpenAPI 객체에 추가
        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components)
                .info(apiInfo());
    }

    private Info apiInfo(){
        return new Info()
                .title("ECC API 문서")
                .description("ECC API 문서입니다.")
                .version("1.0");
    }
}
