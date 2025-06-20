package com.yogendra.finance_tracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Finance Tracker API")
                        .version("1.0.0")
                        .description("Comprehensive API documentation for the Finance Tracker backend. Manage transactions, categories, and users with secure JWT authentication.")
                        .contact(new Contact()
                                .name("Yogendra Singh Baghel")
                                .email("yogendrabaghel2002@gmail.com")
                                .url("https://github.com/yogendra2126/finance-tracker")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .servers(List.of(
                        new Server().url("https://finance-tracker-production-631d.up.railway.app")
                ));
    }
}
