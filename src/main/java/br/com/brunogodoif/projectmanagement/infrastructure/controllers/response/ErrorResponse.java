package br.com.brunogodoif.projectmanagement.infrastructure.controllers.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Response containing error information")
public record ErrorResponse(
        @Schema(description = "Timestamp when the error occurred") LocalDateTime timestamp,

        @Schema(description = "HTTP status code") int status,

        @Schema(description = "Error type") String error,

        @Schema(description = "Error message") String message,

        @Schema(description = "Path of the request that caused the error") String path,

        @Schema(description = "Detailed error information when available") List<String> details
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }

    public ErrorResponse(int status, String error, String message, String path, List<String> details) {
        this(LocalDateTime.now(), status, error, message, path, details);
    }
}