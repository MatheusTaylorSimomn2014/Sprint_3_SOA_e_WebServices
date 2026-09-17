package com.ford.loyaltyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Loyalty Service API",
        version = "1.0.0",
        description = "API para gerenciamento do programa de fidelidade - Ford VIN Share System",
        contact = @Contact(
            name = "Ford VIN Share Team",
            email = "ford-vinshare@ford.com"
        )
    )
)
public class LoyaltyServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoyaltyServiceApplication.class, args);
    }
}
