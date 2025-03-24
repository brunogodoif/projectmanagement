package br.com.brunogodoif.projectmanagement.infrastructure.controllers.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing authentication token information")
public record TokenResponse(
        @Schema(description = "JWT authentication token") String token,

        @Schema(description = "Username of the authenticated user") String username,

        @Schema(description = "Token type, typically 'Bearer'") String type
) {
    public TokenResponse(String token, String username) {
        this(token, username, "Bearer");
    }
}