package br.com.brunogodoif.projectmanagement.domain.dtos;

import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class ProjectInputDTO {
    private UUID id;
    private String name;
    private String description;
    private UUID clientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private String manager;
    private String notes;
}