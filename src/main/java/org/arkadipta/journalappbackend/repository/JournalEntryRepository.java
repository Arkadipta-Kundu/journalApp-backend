package org.arkadipta.journalappbackend.repository;

import org.arkadipta.journalappbackend.entity.JournalEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JournalEntryRepository extends MongoRepository<JournalEntry, String> {
    List<JournalEntry> findByAuthorIdOrderByCreatedAtDesc(String authorId);
}
