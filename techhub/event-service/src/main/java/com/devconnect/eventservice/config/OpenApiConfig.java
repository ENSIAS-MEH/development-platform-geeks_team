package com.devconnect.eventservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;

/** OpenAPI / Swagger configuration for the Event Service. */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Event Service API", version = "1.0", description = "Manages events, hackathons, and registrations"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {}
