package br.com.brunogodoif.projectmanagement.domain.dtos;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class ActivityInputDTO {
    private UUID id;
    private String title;
    private String description;
    private UUID projectId;
    private LocalDate dueDate;
    private String assignedTo;
    private boolean completed;
    private String priority;
    private int estimatedHours;
}