package com.exe.carenest.authorizeservice;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.server.PathContainer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
@EnableMethodSecurity
@Slf4j
@EnableCaching
public class AuthorizeServiceApplication {
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(AuthorizeServiceApplication.class, args);
//        System.out.println(checkPermissions("/api/admin/accounts/1/role"));
    }


    @PostConstruct
    public void showConfiguration() {
        log.info("=== SPRING BOOT CONFIGURATION DEBUG ===");

        // Redis Configuration
        log.info("Redis Host: {}", environment.getProperty("spring.data.redis.host"));
        log.info("Redis Port: {}", environment.getProperty("spring.data.redis.port"));
        log.info("Redis Timeout: {}", environment.getProperty("spring.data.redis.timeout"));

        // Database Configuration
        log.info("Database URL: {}", environment.getProperty("spring.datasource.url"));
        log.info("Database Username: {}", environment.getProperty("spring.datasource.username"));

        // Server Configuration
        log.info("Server Port: {}", environment.getProperty("server.port"));

        // Environment Variables
        log.info("SPRING_REDIS_HOST (env): {}", System.getenv("SPRING_REDIS_HOST"));
        log.info("SPRING_REDIS_PORT (env): {}", System.getenv("SPRING_REDIS_PORT"));

        // Active Profiles
        String[] activeProfiles = environment.getActiveProfiles();
        log.info("Active Profiles: {}", Arrays.toString(activeProfiles));

        log.info("=== END CONFIGURATION DEBUG ===");


    }

    private static final PathPatternParser pathPatternParser = new PathPatternParser();
    private static final List<String> moduleUrls = Arrays.asList(
            "/api/admin/accounts/{id}/role",
            "/api/admin/accounts/{id}"
    );

    private static final String moduleUrl = "/api/admin/accounts/{id}/role";


    public static boolean checkPermission(String actualUrl) {
        return moduleUrls.stream()
                .map(pathPatternParser::parse)
                .anyMatch(pattern -> pattern.matches(PathContainer.parsePath(actualUrl)));
    }


    @Bean
    public RestTemplate initRestTemplate(){
        return new RestTemplate();
    }


}
