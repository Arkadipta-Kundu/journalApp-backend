package org.arkadipta.journalappbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.arkadipta.journalappbackend.dto.*;
import org.arkadipta.journalappbackend.entity.User;
import org.arkadipta.journalappbackend.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Configure properly in production
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable String userId) {
        try {
            UserResponse user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId,
            @Valid @RequestBody UserRegistrationRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Check authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authorization header required"));
        }

        String token = authHeader.substring(7);
        Optional<User> userOpt = userService.getUserBySessionToken(token);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid or expired session"));
        }

        // Check if user can update this profile (only themselves)
        if (!userOpt.get().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You can only update your own profile"));
        }

        try {
            UserResponse updatedUser = userService.updateUser(userId, request);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        // Check authentication
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Authorization header required"));
        }

        String token = authHeader.substring(7);
        Optional<User> userOpt = userService.getUserBySessionToken(token);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid or expired session"));
        }

        // Check if user can delete this profile (only themselves)
        if (!userOpt.get().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("You can only delete your own profile"));
        }

        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new SuccessResponse("User deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
