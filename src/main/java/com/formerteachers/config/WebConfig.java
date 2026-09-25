package com.formerteachers.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String uploadPath = Paths.get("uploads").toAbsolutePath().normalize().toUri().toString();

        logger.info("Serving /uploads/** from: {}", uploadPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:/app/uploads/");
    }
}