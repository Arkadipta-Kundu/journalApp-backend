package org.arkadipta.journalappbackend.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/public/api")
@CrossOrigin(origins = "*") // Configure properly in production
public class Public {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
                "status", "healthy",
                "message", "Journal App Backend is running",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0-PHASE1");
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "name", "Journal App Backend",
                "description", "A secure journaling backend with social features",
                "phase", "Phase 1 - Core Features",
                "features", new String[] {
                        "User Registration & Login",
                        "Journal Entry CRUD",
                        "Basic Authentication",
                        "Password Hashing"
                },
                "environment", detectEnvironment(),
                "env_vars_status", Map.of(
                        "mongodb_uri_set", System.getenv("MONGODB_URI") != null,
                        "session_secret_set", System.getenv("SESSION_SECRET") != null,
                        "websites_port_set", System.getenv("WEBSITES_PORT") != null));
    }

    private String detectEnvironment() {
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
