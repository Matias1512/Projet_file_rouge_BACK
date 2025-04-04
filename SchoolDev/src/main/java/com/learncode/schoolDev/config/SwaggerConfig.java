package com.learncode.schoolDev.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl("https://schooldev.duckdns.org");
        server.setDescription("Serveur principal");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("SchoolDev API")
                        .version("1.0")
                        .description("Documentation de l’API SchoolDev"));
    }
}
