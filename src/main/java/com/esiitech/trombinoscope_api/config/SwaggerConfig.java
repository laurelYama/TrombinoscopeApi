package com.esiitech.trombinoscope_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "BearerAuth",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
@OpenAPIDefinition(
        info = @Info(
                title = "Trombinoscope API",
                version = "1.0",
                description = "API de gestion du trombinoscope d'ESIITECH",
                contact = @Contact(
                        name = "ESIITECH Support",
                        email = "contact@esiitech-gabon.com",
                        url = "https://www.esiitech-gabon.com"
                )

        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Serveur local"),
                @Server(url = "https://api.esiitech.com", description = "Serveur de production")
        }
)
public class SwaggerConfig {
}
