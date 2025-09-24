package com.exe.carenest.authorizeservice;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//Import
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.Arrays;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableMethodSecurity
@Slf4j
public class AuthorizeServiceApplication {
	@Autowired
	private Environment environment;

	public static void main(String[] args) {
		SpringApplication.run(AuthorizeServiceApplication.class, args);
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
}
