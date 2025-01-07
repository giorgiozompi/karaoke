package com.example.karaoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class KaraokeApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(KaraokeApplication.class, args);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configurazione per servire le immagini caricate
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}