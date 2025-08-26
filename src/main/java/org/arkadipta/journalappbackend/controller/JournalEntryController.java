package org.arkadipta.journalappbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.arkadipta.journalappbackend.dto.*;
import org.arkadipta.journalappbackend.entity.User;
import org.arkadipta.journalappbackend.service.JournalEntryService;
import org.arkadipta.journalappbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/entries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure properly in production
public class JournalEntryController {

    private final JournalEntryService journalEntryService;
    private final UserService userService;

    // Helper method to authenticate user
    private Optional<User> authenticateUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = authHeader.substring(7);
        return userService.getUserBySessionToken(token);
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@Valid @RequestBody JournalEntryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Optional<User> userOpt = authenticateUser(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required"));
        }

        try {
            JournalEntryResponse entry = journalEntryService.createEntry(userOpt.get().getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getCurrentUserEntries(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Optional<User> userOpt = authenticateUser(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required"));
        }

        List<JournalEntryResponse> entries = journalEntryService.getUserEntries(userOpt.get().getId());
        return ResponseEntity.ok(entries);
    }

    @GetMapping("/{entryId}")
    public ResponseEntity<?> getEntryById(@PathVariable String entryId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Optional<User> userOpt = authenticateUser(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required"));
        }

        try {
            JournalEntryResponse entry = journalEntryService.getEntryById(entryId, userOpt.get().getId());
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{entryId}")
    public ResponseEntity<?> updateEntry(@PathVariable String entryId,
            @Valid @RequestBody JournalEntryRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Optional<User> userOpt = authenticateUser(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required"));
        }

        try {
            JournalEntryResponse entry = journalEntryService.updateEntry(entryId, userOpt.get().getId(), request);
            return ResponseEntity.ok(entry);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{entryId}")
    public ResponseEntity<?> deleteEntry(@PathVariable String entryId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Optional<User> userOpt = authenticateUser(authHeader);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required"));
        }

        try {
            journalEntryService.deleteEntry(entryId, userOpt.get().getId());
            return ResponseEntity.ok(new SuccessResponse("Entry deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
