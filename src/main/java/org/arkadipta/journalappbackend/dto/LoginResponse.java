package org.arkadipta.journalappbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String sessionToken;
    private UserResponse user;
}
