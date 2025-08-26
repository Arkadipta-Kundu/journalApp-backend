package org.arkadipta.journalappbackend.config;

import lombok.RequiredArgsConstructor;
import org.arkadipta.journalappbackend.service.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {

    private final UserService userService;

    // Clean up expired sessions every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    public void cleanupExpiredSessions() {
        userService.cleanupExpiredSessions();
    }
}
