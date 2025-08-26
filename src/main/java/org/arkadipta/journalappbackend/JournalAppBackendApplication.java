package org.arkadipta.journalappbackend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class JournalAppBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(JournalAppBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner logStartupInfo() {
        return args -> {
            log.info("=== Journal App Backend Startup Info ===");

            // Log environment detection
            String environment = detectEnvironment();
            log.info("Detected environment: {}", environment);

            // Log configuration status (without exposing sensitive data)
            log.info("MongoDB Database: {}",
                    System.getProperty("spring.data.mongodb.database", "NOT SET"));

            log.info("Server Port: {}",
                    System.getProperty("server.port", System.getenv("PORT")));

            // Log environment variable status
            log.info("MONGODB_URI configured: {}",
                    System.getenv("MONGODB_URI") != null ? "YES" : "NO");

            log.info("SESSION_SECRET configured: {}",
                    System.getenv("SESSION_SECRET") != null ? "YES" : "NO");

            if ("Azure App Service".equals(environment)) {
                log.info("Azure-specific variables:");
                log.info("  WEBSITES_PORT: {}", System.getenv("WEBSITES_PORT"));
                log.info("  WEBSITE_HOSTNAME: {}", System.getenv("WEBSITE_HOSTNAME"));
            }

            log.info("=== Startup Info Complete ===");
        };
    }

    private static String detectEnvironment() {
        if (System.getenv("WEBSITE_HOSTNAME") != null) {
            return "Azure App Service";
        } else if (System.getenv("COMPUTERNAME") != null) {
            return "Windows Local";
        } else if (System.getenv("HOSTNAME") != null) {
            return "Linux/Unix Local";
        } else {
            return "Unknown";
        }
    }

}
