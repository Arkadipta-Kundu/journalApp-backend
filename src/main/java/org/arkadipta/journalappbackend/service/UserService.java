package org.arkadipta.journalappbackend.service;

import lombok.RequiredArgsConstructor;
import org.arkadipta.journalappbackend.dto.*;
import org.arkadipta.journalappbackend.entity.Session;
import org.arkadipta.journalappbackend.entity.User;
import org.arkadipta.journalappbackend.repository.SessionRepository;
import org.arkadipta.journalappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${app.session-timeout:3600}")
    private int sessionTimeout;

    public UserResponse registerUser(UserRegistrationRequest request) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new user with hashed password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        User user = new User(request.getUsername(), request.getEmail(), hashedPassword);
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getUsername(),
                savedUser.getEmail(), savedUser.getCreatedAt());
    }

    public LoginResponse loginUser(UserLoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = userOpt.get();

        // Clean up existing sessions for this user
        sessionRepository.deleteByUserId(user.getId());

        // Create new session
        String sessionToken = UUID.randomUUID().toString();
        Session session = new Session(sessionToken, user.getId(), sessionTimeout);
        sessionRepository.save(session);

        UserResponse userResponse = new UserResponse(user.getId(), user.getUsername(),
                user.getEmail(), user.getCreatedAt());

        return new LoginResponse(sessionToken, userResponse);
    }

    public Optional<User> getUserBySessionToken(String sessionToken) {
        if (sessionToken == null) {
            return Optional.empty();
        }

        Optional<Session> sessionOpt = sessionRepository.findBySessionToken(sessionToken);

        if (sessionOpt.isEmpty() || sessionOpt.get().isExpired()) {
            // Clean up expired session
            sessionOpt.ifPresent(sessionRepository::delete);
            return Optional.empty();
        }

        return userRepository.findById(sessionOpt.get().getUserId());
    }

    public UserResponse getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserResponse(user.getId(), user.getUsername(),
                user.getEmail(), user.getCreatedAt());
    }

    public UserResponse updateUser(String userId, UserRegistrationRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if new username/email already exists (excluding current user)
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getUsername(),
                savedUser.getEmail(), savedUser.getCreatedAt());
    }

    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Clean up sessions
        sessionRepository.deleteByUserId(userId);

        // Delete user
        userRepository.delete(user);
    }

    public void logout(String sessionToken) {
        sessionRepository.findBySessionToken(sessionToken)
                .ifPresent(sessionRepository::delete);
    }

    public void cleanupExpiredSessions() {
        sessionRepository.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
