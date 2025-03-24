package br.com.brunogodoif.projectmanagement.infrastructure.controllers.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Response containing activity information")
public record ActivityResponse(
        @Schema(description = "Unique identifier of the activity") UUID id,

        @Schema(description = "Title of the activity") String title,

        @Schema(description = "Detailed description of the activity") String description,

        @Schema(description = "ID of the project this activity belongs to") UUID projectId,

        @Schema(description = "Name of the project this activity belongs to") String projectName,

        @Schema(description = "Due date for the activity") LocalDate dueDate,

        @Schema(description = "Person assigned to the activity") String assignedTo,

        @Schema(description = "Whether the activity is completed") boolean completed,

        @Schema(description = "Priority level of the activity (HIGH, MEDIUM, LOW)") String priority,

        @Schema(description = "Estimated hours to complete the activity") int estimatedHours,

        @Schema(description = "Date and time when the activity was created") LocalDateTime createdAt,

        @Schema(description = "Date and time when the activity was last updated") LocalDateTime updatedAt
) {
}