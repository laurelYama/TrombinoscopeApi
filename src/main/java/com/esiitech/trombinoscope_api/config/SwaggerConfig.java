package com.esiitech.trombinoscope_api.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Trombinoscope API",
                version = "1.0",
                description = "API de gestion du trombinoscope d'ESIITECH",
                contact = @Contact(
                        name = "ESIITECH Support",
                        email = "support@esiitech.com",
                        url = "https://www.esiitech.com"
                )

        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Serveur local"),
                @Server(url = "https://api.esiitech.com", description = "Serveur de production")
        }
)
public class SwaggerConfig {
}
