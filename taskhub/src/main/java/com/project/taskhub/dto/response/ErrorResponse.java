package com.project.taskhub.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        int status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
                LocalDateTime timestamp,
        Map<String, String> errors) {

    public ErrorResponse(String message, int status, LocalDateTime timestamp) {
        this(message, status, timestamp, Map.of());
    }
}
