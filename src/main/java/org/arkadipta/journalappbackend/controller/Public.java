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
                });
    }
}
