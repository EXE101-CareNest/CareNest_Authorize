package com.exe.carenest.authorizeservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Value("${app.version}")
    private String appVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CareNest Auth Service API")
                        .description("API documentation for CareNest Auth Service - Auth management")
                        .version(appVersion)
                        .contact(new Contact()
                                .name("CareNest Team")
                                .email("support@carenest.com")
                                .url("https://carenest.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
//                .servers(List.of(
//                        new Server().url("http://localhost:8080").description("Local Development Server"),
//                        new Server().url("http://localhost:8082").description("Local Development Server"),
//                        new Server().url("http://103.252.95.124:8080").description("Production Server")
//                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );

    }
}
