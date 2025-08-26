package org.arkadipta.journalappbackend.controller;

import lombok.RequiredArgsConstructor;
import org.arkadipta.journalappbackend.dto.ErrorResponse;
import org.arkadipta.journalappbackend.dto.JournalEntryResponse;
import org.arkadipta.journalappbackend.entity.User;
import org.arkadipta.journalappbackend.service.JournalEntryService;
import org.arkadipta.journalappbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/{userId}/entries")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure properly in production
public class UserEntriesController {

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

    @GetMapping
    public ResponseEntity<?> getUserEntries(@PathVariable String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Optional<User> currentUserOpt = authenticateUser(authHeader);
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authentication required"));
        }

        // In Phase 1, users can only view their own entries
        if (!currentUserOpt.get().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You can only view your own entries"));
        }

        // Verify the target user exists
        try {
            userService.getUserById(userId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        List<JournalEntryResponse> entries = journalEntryService.getUserEntries(userId);
        return ResponseEntity.ok(entries);
    }
}
