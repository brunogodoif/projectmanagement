package br.com.brunogodoif.projectmanagement.infrastructure.controllers.request;

import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "Request object for creating or updating a project")
public record ProjectRequest(
        @Schema(description = "Name of the project", required = true, example = "Website Redesign") @NotBlank(message = "Project name is required") String name,

        @Schema(description = "Detailed description of the project", required = true, example = "Complete redesign of the company website with new branding") @NotBlank(message = "Project description is required") String description,

        @Schema(description = "ID of the client this project belongs to", required = true, example = "123e4567-e89b-12d3-a456-426614174000") @NotNull(message = "Client ID is required") UUID clientId,

        @Schema(description = "Start date of the project", required = true, example = "2025-06-01") @NotNull(message = "Start date is required") LocalDate startDate,

        @Schema(description = "End date of the project", required = true, example = "2025-12-31") @NotNull(message = "End date is required") LocalDate endDate,

        @Schema(description = "Status of the project (OPEN, IN_PROGRESS, ON_HOLD, COMPLETED, CANCELLED)", required = true, example = "IN_PROGRESS") @NotNull(message = "Status is required") ProjectStatus status,

        @Schema(description = "Project manager's name", required = true, example = "Jane Smith") @NotBlank(message = "Manager name is required") String manager,

        @Schema(description = "Additional notes about the project", required = true, example = "Priority project for Q3") @NotBlank(message = "Notes are required") String notes
) {
}