package com.openPay.marvel.MarvelApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("Origin", "Content-Type", "Accept", "Authorization")
                .exposedHeaders("Authorization", "Cache-Control", "Content-Language", "Content-Type", "Expires", "Last-Modified", "Pragma")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
