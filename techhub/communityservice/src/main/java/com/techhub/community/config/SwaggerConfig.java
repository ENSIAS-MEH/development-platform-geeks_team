package com.techhub.community.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI communityServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TechHub Community Service API")
                        .description(
                                "REST API for managing community groups, posts, comments and member interactions within the TechHub platform.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("TechHub Team – Member 4")
                                .email("community@techhub.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8085").description("Local dev"),
                        new Server().url("http://community-service:8085").description("Docker network")));
    }
}
