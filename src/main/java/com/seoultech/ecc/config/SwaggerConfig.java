package com.seoultech.ecc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Todo: JWT 적용 후 주석 제거

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
//        String jwt = "JWT";
//        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
//        Components components = new Components().addSecuritySchemes(jwt, new SecurityScheme()
//                .name(jwt)
//                .type(SecurityScheme.Type.HTTP)
//                .scheme("bearer")
//                .bearerFormat("JWT")
//        );
        return new OpenAPI()
                .components(new Components()) // components로 대체
                .info(apiInfo()); // .addSecurityItem(securityRequirement);
    }

    private Info apiInfo(){
        return new Info()
                .title("ECC API 문서")
                .description("ECC API 문서입니다.")
                .version("1.0");
    };
}
