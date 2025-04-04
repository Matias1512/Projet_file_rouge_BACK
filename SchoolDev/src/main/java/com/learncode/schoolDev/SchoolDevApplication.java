package com.learncode.schoolDev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "API Documentation",
        version = "v1"
    ),
    servers = @Server(
        url = "https://schooldev.duckdns.org",
        description = "Serveur principal"
    )
)
public class SchoolDevApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolDevApplication.class, args);
	}

}
