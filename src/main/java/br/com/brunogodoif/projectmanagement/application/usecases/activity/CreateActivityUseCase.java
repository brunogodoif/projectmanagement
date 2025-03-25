package br.com.brunogodoif.projectmanagement.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.CreateActivityInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CreateActivityUseCase implements CreateActivityInterface {

    private final ActivityGatewayInterface activityGateway;
    private final ProjectGatewayInterface projectGateway;

    public CreateActivityUseCase(ActivityGatewayInterface activityGateway, ProjectGatewayInterface projectGateway) {
        this.activityGateway = activityGateway;
        this.projectGateway = projectGateway;
    }

    @Override
    public Activity execute(ActivityInputDTO activityInputDTO) {
        log.info("Creating new activity: {}", activityInputDTO.getTitle());

        try {
            Project project = projectGateway.findById(activityInputDTO.getProjectId())
                                            .orElseThrow(() -> new EntityNotFoundException(
                                                    "Project not found with ID: " + activityInputDTO.getProjectId()));

            Activity activity = new Activity(
                    activityInputDTO.getId(),
                    activityInputDTO.getTitle(),
                    activityInputDTO.getDescription(),
                    project,
                    activityInputDTO.getDueDate(),
                    activityInputDTO.getAssignedTo(),
                    activityInputDTO.isCompleted(),
                    activityInputDTO.getPriority(),
                    activityInputDTO.getEstimatedHours(),
                    null,
                    null
            );

            return activityGateway.save(activity);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to create activity", e);
        }
    }
}