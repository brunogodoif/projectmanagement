package br.com.brunogodoif.projectmanagement.infrastructure.controllers.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request object for creating or updating a client")
public record ClientRequest(
        @Schema(description = "Name of the client", required = true, example = "ABC Corporation") @NotBlank(message = "Client name is required") String name,

        @Schema(description = "Email address of the client", required = true, example = "contact@abccorp.com") @NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

        @Schema(description = "Phone number of the client", required = true, example = "555-123-4567") @NotBlank(message = "Phone number is required") String phone,

        @Schema(description = "Company name of the client", required = true, example = "ABC Corporation Inc.") @NotBlank(message = "Company name is required") String companyName,

        @Schema(description = "Physical address of the client", required = true, example = "123 Main St, Anytown, USA") @NotBlank(message = "Address is required") String address,

        @Schema(description = "Client status (active/inactive)", defaultValue = "true", example = "true") boolean active
) {
}