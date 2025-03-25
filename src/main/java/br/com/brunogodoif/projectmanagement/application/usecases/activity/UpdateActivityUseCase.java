package br.com.brunogodoif.projectmanagement.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.UpdateActivityInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Log4j2
public class UpdateActivityUseCase implements UpdateActivityInterface {

    private final ActivityGatewayInterface activityGateway;
    private final ProjectGatewayInterface projectGateway;

    public UpdateActivityUseCase(ActivityGatewayInterface activityGateway, ProjectGatewayInterface projectGateway) {
        this.activityGateway = activityGateway;
        this.projectGateway = projectGateway;
    }

    @Override
    public Activity execute(UUID id, ActivityInputDTO activityInputDTO) {
        log.info("Updating activity with ID: {}", id);

        try {
            Activity existingActivity = activityGateway.findById(id)
                                                       .orElseThrow(() -> new EntityNotFoundException("Activity not found with ID: " + id));

            Project project;

            if (!existingActivity.getProject().getId().equals(activityInputDTO.getProjectId())) {
                project = projectGateway.findById(activityInputDTO.getProjectId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                "Project not found with ID: " + activityInputDTO.getProjectId()));
            } else {
                project = existingActivity.getProject();
            }

            Activity updatedActivity = new Activity(
                    existingActivity.getId(),
                    activityInputDTO.getTitle(),
                    activityInputDTO.getDescription(),
                    project,
                    activityInputDTO.getDueDate(),
                    activityInputDTO.getAssignedTo(),
                    activityInputDTO.isCompleted(),
                    activityInputDTO.getPriority(),
                    activityInputDTO.getEstimatedHours(),
                    existingActivity.getCreatedAt(),
                    LocalDateTime.now()
            );

            return activityGateway.save(updatedActivity);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to update activity", e);
        }
    }
}