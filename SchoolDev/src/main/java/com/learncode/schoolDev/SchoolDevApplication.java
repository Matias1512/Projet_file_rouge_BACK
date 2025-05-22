package com.learncode.schoolDev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Documentation de l'API",
        version = "v1"
    ),
    servers = {
        @Server(url = "http://schooldev.duckdns.org", description = "Serveur HTTP"),
        @Server(url = "https://schooldev.duckdns.org", description = "Serveur HTTPS")
    }
)

@SpringBootApplication
public class SchoolDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolDevApplication.class, args);
	}

}
