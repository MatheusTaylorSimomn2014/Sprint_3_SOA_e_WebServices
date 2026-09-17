package com.ford.vinservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "VIN Service API",
        version = "1.0.0",
        description = "API para gerenciamento de veículos via VIN - Ford VIN Share System",
        contact = @Contact(
            name = "Ford VIN Share Team",
            email = "ford-vinshare@ford.com"
        )
    )
)
public class VinServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VinServiceApplication.class, args);
    }
}
