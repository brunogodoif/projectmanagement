package br.com.brunogodoif.projectmanagement.infrastructure.controllers.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Schema(description = "Request object for creating or updating a user")
public record UserRequest(
        @Schema(description = "Username for the user", required = true, example = "johndoe")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Password for the user", required = true)
        @NotBlank(message = "Password is required")
        String password,

        @Schema(description = "Email address of the user", required = true, example = "john.doe@example.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @Schema(description = "Full name of the user", required = true, example = "John Doe")
        @NotBlank(message = "Full name is required")
        String fullName,

        @Schema(description = "Roles assigned to the user (ADMIN, USER, BACKOFFICE, etc.)", example = "[\"ADMIN\", \"USER\"]") List<String> roles
) {
    public UserRequest(String username, String password, String email, String fullName) {
        this(username, password, email, fullName, List.of("USER"));
    }
}