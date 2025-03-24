package br.com.brunogodoif.projectmanagement.infrastructure.controllers.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Detailed response containing client information with associated projects")
public record ClientDetailResponse(
        @Schema(description = "Unique identifier of the client") UUID id,

        @Schema(description = "Name of the client") String name,

        @Schema(description = "Email address of the client") String email,

        @Schema(description = "Phone number of the client") String phone,

        @Schema(description = "Company name of the client") String companyName,

        @Schema(description = "Physical address of the client") String address,

        @Schema(description = "List of projects associated with this client") List<ProjectResponse> projects,

        @Schema(description = "Date and time when the client was created") LocalDateTime createdAt,

        @Schema(description = "Date and time when the client was last updated") LocalDateTime updatedAt,

        @Schema(description = "Whether the client is active") boolean active
) {
}