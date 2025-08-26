package org.arkadipta.journalappbackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JournalEntryRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 140, message = "Title must not exceed 140 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(max = 20000, message = "Content must not exceed 20,000 characters")
    private String content;
}
