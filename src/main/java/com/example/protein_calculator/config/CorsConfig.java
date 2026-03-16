package com.example.protein_calculator.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(CorsConfig.CorsProperties.class)
public class CorsConfig implements WebMvcConfigurer {

    private static final List<String> ALLOWED_METHODS =
        List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    private final CorsProperties properties;

    public CorsConfig(CorsProperties properties) {
        this.properties = properties;

        boolean hasWildcard = properties.allowedOriginPatterns().stream().anyMatch("*"::equals);
        if (hasWildcard && properties.allowCredentials()) {
            throw new IllegalStateException(
                "Invalid CORS config: allowCredentials=true cannot be used with allowed-origin-patterns='*'. " +
                    "Either set allow-credentials=false for wildcard testing, or list explicit origins for production."
            );
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
        .allowedOriginPatterns(properties.allowedOriginPatterns().toArray(String[]::new))
        .allowedMethods(ALLOWED_METHODS.toArray(String[]::new))
                .allowedHeaders("*")
        .allowCredentials(properties.allowCredentials())
        .maxAge(properties.maxAgeSeconds());
    }

    /**
     * CORS configuration source used by Spring Security if/when you add it.
     * (With Spring Security present you typically call http.cors(...) in SecurityConfig.)
     */
    @org.springframework.context.annotation.Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(properties.allowedOriginPatterns());
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(properties.allowCredentials());
        config.setMaxAge(properties.maxAgeSeconds());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Property-driven CORS settings.
     *
     * Spring Boot relaxed binding means these can be controlled via:
     * - application.properties: app.cors.allowed-origin-patterns=...
     * - Azure App Service App Settings env vars:
     *     APP_CORS_ALLOWED_ORIGIN_PATTERNS
     *     APP_CORS_ALLOW_CREDENTIALS
     */
    @ConfigurationProperties(prefix = "app.cors")
    public record CorsProperties(
            List<String> allowedOriginPatterns,
            boolean allowCredentials,
            long maxAgeSeconds
    ) {
        public CorsProperties {
            if (allowedOriginPatterns == null || allowedOriginPatterns.isEmpty()) {
                allowedOriginPatterns = List.of("*");
            }
            if (maxAgeSeconds <= 0) {
                maxAgeSeconds = 3600L;
            }
        }
    }
}