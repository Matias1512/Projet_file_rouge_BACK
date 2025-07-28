package com.learncode.schoolDev.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server prodServer = new Server();
        prodServer.setUrl("https://schooldev.duckdns.org");
        prodServer.setDescription("Serveur principal");
        
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Serveur de développement");

        SecurityScheme jwtScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
        .in(SecurityScheme.In.HEADER)
        .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        return new OpenAPI()
            .addSecurityItem(securityRequirement)
            .components(new Components().addSecuritySchemes("bearerAuth", jwtScheme))
            .servers(List.of(prodServer, localServer))
            .info(new Info()
                .title("SchoolDev API")
                .version("1.0")
                .description("Documentation de l’API SchoolDev"));
    }
}