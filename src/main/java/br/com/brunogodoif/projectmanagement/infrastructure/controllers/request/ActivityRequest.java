package br.com.brunogodoif.projectmanagement.infrastructure.controllers.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request object for creating or updating an activity")
public record ActivityRequest(
        @Schema(description = "Title of the activity", required = true, example = "Implement user authentication") @NotBlank(message = "Activity title is required") String title,

        @Schema(description = "Detailed description of the activity", required = true, example = "Implement JWT authentication flow with refresh tokens") @NotBlank(message = "Activity description is required") String description,

        @Schema(description = "ID of the project this activity belongs to", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @NotNull(message = "Project ID is required") UUID projectId,

        @Schema(description = "Due date for the activity", required = true, example = "2025-12-31") @NotNull(message = "Due date is required") LocalDate dueDate,

        @Schema(description = "Person assigned to the activity", required = true, example = "John Doe") @NotBlank(message = "Assigned person is required") String assignedTo,

        @Schema(description = "Whether the activity is completed", required = true, example = "false") boolean completed,

        @Schema(description = "Priority level of the activity (HIGH, MEDIUM, LOW)", required = true, example = "HIGH") @NotBlank(message = "Priority is required") String priority,

        @Schema(description = "Estimated hours to complete the activity", required = true, example = "8") @NotNull(message = "Estimated hours is required") @Min(value = 0, message = "Estimated hours cannot be negative") int estimatedHours
) {
}