package com.project.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "BSR COMMERCE API",
        version = "1.0",
        description = "API REST para la gestión integral de una plataforma e-commerce. Permite la administración de usuarios, productos, categorías, pedidos y pagos, incluyendo autenticación basada en JWT y control de acceso seguro."
    )
)
public class SwaggerConfig {
}