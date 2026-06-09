package com.techhub.notification_service.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotEnvConfig {

    static {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()  // Ignore if .env file is missing
                .load();  // Load environment variables from .env file
        dotenv.entries().forEach(entry ->
                System.setProperty(entry.getKey(), entry.getValue())
        ); // Set each environment variable as a system property , system properties can be accessed using System.getProperty("KEY") or System.getenv("KEY")
    }
    // @Bean annotation is used to indicate that a method produces a bean to be managed by the Spring container. In this case, it creates a Dotenv bean that can be injected into other components of the application.
    @Bean
    public Dotenv dotenv() {
        return Dotenv.configure().ignoreIfMissing().load();
    }
}