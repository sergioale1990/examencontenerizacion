package com.luzam.contenerizacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI tareasOpenApi(@Value("${app.version:1.0.0}") String version,
                                 @Value("${app.entorno:local}") String entorno) {
        return new OpenAPI().info(new Info()
                .title("API de gestión de tareas")
                .version(version)
                .description("MIS-709 Contenerización y Orquestación · entorno: " + entorno)
                .contact(new Contact().name("Sergio Alejandro Lema Estevez")));
    }
}