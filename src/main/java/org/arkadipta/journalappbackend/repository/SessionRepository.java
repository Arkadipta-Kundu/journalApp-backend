package org.arkadipta.journalappbackend.repository;

import org.arkadipta.journalappbackend.entity.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    Optional<Session> findBySessionToken(String sessionToken);

    void deleteByUserId(String userId);

    void deleteByExpiresAtBefore(LocalDateTime now);
}
