package br.com.brunogodoif.projectmanagement.infrastructure.controllers.response;

import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response containing project information")
public record ProjectResponse(
        @Schema(description = "Unique identifier of the project") UUID id,

        @Schema(description = "Name of the project") String name,

        @Schema(description = "Detailed description of the project") String description,

        @Schema(description = "ID of the client this project belongs to") UUID clientId,

        @Schema(description = "Name of the client this project belongs to") String clientName,

        @Schema(description = "Start date of the project") LocalDate startDate,

        @Schema(description = "End date of the project") LocalDate endDate,

        @Schema(description = "Current status of the project (OPEN, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED)") ProjectStatus status,

        @Schema(description = "Project manager's name") String manager,

        @Schema(description = "Additional notes about the project") String notes,

        @Schema(description = "Date and time when the project was created") LocalDateTime createdAt,

        @Schema(description = "Date and time when the project was last updated") LocalDateTime updatedAt
) {
}