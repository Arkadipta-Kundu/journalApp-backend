package org.arkadipta.journalappbackend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/public/api")
public class DiagnosticsController {

    @Value("${spring.data.mongodb.database}")
    private String mongoDatabase;

    @Value("${app.session-timeout}")
    private String sessionTimeout;

    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/config-info")
    public Map<String, Object> getConfigInfo() {
        Map<String, Object> configInfo = new HashMap<>();

        // Safe information that can be exposed
        configInfo.put("database_name", mongoDatabase);
        configInfo.put("session_timeout", sessionTimeout);
        configInfo.put("server_port", serverPort);

        // Check if MongoDB URI is being read (without exposing the actual URI)
        String mongoUri = System.getenv("MONGODB_URI");
        configInfo.put("mongodb_uri_configured", mongoUri != null && !mongoUri.isEmpty());
        configInfo.put("mongodb_uri_length", mongoUri != null ? mongoUri.length() : 0);

        // Check if session secret is being read (without exposing it)
        String sessionSecret = System.getenv("SESSION_SECRET");
        configInfo.put("session_secret_configured", sessionSecret != null && !sessionSecret.isEmpty());
        configInfo.put("session_secret_length", sessionSecret != null ? sessionSecret.length() : 0);

        // Environment detection
        configInfo.put("environment", detectEnvironment());

        // Available environment variables (filtered for safety)
        Map<String, String> filteredEnvVars = new HashMap<>();
        System.getenv().forEach((key, value) -> {
            if (key.startsWith("MONGODB_") || key.startsWith("SESSION_") ||
                    key.startsWith("PORT") || key.startsWith("WEBSITES_")) {
                // Show that the variable exists but not its value
                filteredEnvVars.put(key, value != null ? "[SET - Length: " + value.length() + "]" : "[NOT SET]");
            }
        });
        configInfo.put("environment_variables", filteredEnvVars);

        return configInfo;
    }

    @GetMapping("/env-test")
    public Map<String, Object> testEnvironmentVariables() {
        Map<String, Object> envTest = new HashMap<>();

        // Test each required environment variable
        envTest.put("MONGODB_URI", System.getenv("MONGODB_URI") != null ? "✅ SET" : "❌ NOT SET");
        envTest.put("MONGODB_DATABASE", System.getenv("MONGODB_DATABASE") != null ? "✅ SET" : "❌ NOT SET");
        envTest.put("SESSION_SECRET", System.getenv("SESSION_SECRET") != null ? "✅ SET" : "❌ NOT SET");
        envTest.put("SESSION_TIMEOUT", System.getenv("SESSION_TIMEOUT") != null ? "✅ SET" : "❌ NOT SET");
        envTest.put("PORT", System.getenv("PORT") != null ? "✅ SET" : "❌ NOT SET");

        // Azure-specific variables
        envTest.put("WEBSITES_PORT", System.getenv("WEBSITES_PORT") != null ? "✅ SET" : "❌ NOT SET");
        envTest.put("WEBSITE_HOSTNAME", System.getenv("WEBSITE_HOSTNAME") != null ? "✅ SET" : "❌ NOT SET");

        return envTest;
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
