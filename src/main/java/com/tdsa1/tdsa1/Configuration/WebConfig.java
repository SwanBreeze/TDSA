package com.tdsa1.tdsa1.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Allow all paths
                .allowedOrigins("http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8080") // frontend URL
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")// Allow all headers

                .allowCredentials(true); // If you're using cookies or auth headers


    }


}

