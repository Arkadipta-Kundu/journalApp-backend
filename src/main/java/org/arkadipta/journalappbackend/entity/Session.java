package org.arkadipta.journalappbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sessions")
public class Session {
    @Id
    private String id;

    private String sessionToken;

    private String userId;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    public Session(String sessionToken, String userId, int timeoutInSeconds) {
        this.sessionToken = sessionToken;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusSeconds(timeoutInSeconds);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
