package br.com.brunogodoif.projectmanagement.infrastructure.controllers.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(description = "Request object for user authentication")
public record AuthRequest(
        @Schema(description = "Username for authentication", required = true, example = "admin") @NotBlank(message = "Username is required") String username,

        @Schema(description = "User password", required = true, example = "password") @NotBlank(message = "Password is required") String password
) {
}