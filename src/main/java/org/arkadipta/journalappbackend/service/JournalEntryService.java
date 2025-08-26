package org.arkadipta.journalappbackend.service;

import lombok.RequiredArgsConstructor;
import org.arkadipta.journalappbackend.dto.JournalEntryRequest;
import org.arkadipta.journalappbackend.dto.JournalEntryResponse;
import org.arkadipta.journalappbackend.entity.JournalEntry;
import org.arkadipta.journalappbackend.repository.JournalEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;

    public JournalEntryResponse createEntry(String authorId, JournalEntryRequest request) {
        JournalEntry entry = new JournalEntry(authorId, request.getTitle(), request.getContent());
        JournalEntry savedEntry = journalEntryRepository.save(entry);

        return new JournalEntryResponse(savedEntry.getId(), savedEntry.getAuthorId(),
                savedEntry.getTitle(), savedEntry.getContent(),
                savedEntry.getCreatedAt(), savedEntry.getUpdatedAt());
    }

    public List<JournalEntryResponse> getUserEntries(String authorId) {
        List<JournalEntry> entries = journalEntryRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);

        return entries.stream()
                .map(entry -> new JournalEntryResponse(entry.getId(), entry.getAuthorId(),
                        entry.getTitle(), entry.getContent(),
                        entry.getCreatedAt(), entry.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    public JournalEntryResponse getEntryById(String entryId, String userId) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));

        // Only the author can view their entries in Phase 1
        if (!entry.getAuthorId().equals(userId)) {
            throw new RuntimeException("Access denied - you can only view your own entries");
        }

        return new JournalEntryResponse(entry.getId(), entry.getAuthorId(),
                entry.getTitle(), entry.getContent(),
                entry.getCreatedAt(), entry.getUpdatedAt());
    }

    public JournalEntryResponse updateEntry(String entryId, String userId, JournalEntryRequest request) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));

        // Only the author can modify their entries
        if (!entry.getAuthorId().equals(userId)) {
            throw new RuntimeException("Access denied - you can only modify your own entries");
        }

        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setUpdatedAt(LocalDateTime.now());

        JournalEntry savedEntry = journalEntryRepository.save(entry);

        return new JournalEntryResponse(savedEntry.getId(), savedEntry.getAuthorId(),
                savedEntry.getTitle(), savedEntry.getContent(),
                savedEntry.getCreatedAt(), savedEntry.getUpdatedAt());
    }

    public void deleteEntry(String entryId, String userId) {
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Journal entry not found"));

        // Only the author can delete their entries
        if (!entry.getAuthorId().equals(userId)) {
            throw new RuntimeException("Access denied - you can only delete your own entries");
        }

        journalEntryRepository.delete(entry);
    }
}
