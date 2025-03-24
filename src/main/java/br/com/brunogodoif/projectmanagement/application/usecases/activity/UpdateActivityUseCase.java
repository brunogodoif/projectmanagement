package br.com.brunogodoif.projectmanagement.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.UpdateActivityInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
    public Activity execute(UUID id, Activity updatedActivity) {
        log.info("Updating activity with ID: {}", id);

        try {
            Activity existingActivity = activityGateway.findById(id).orElseThrow(() -> new EntityNotFoundException(
                    "Activity not found with ID: " + id));

            if (updatedActivity.getProject() != null && !existingActivity.getProject().getId()
                                                                         .equals(updatedActivity.getProject()
                                                                                                .getId())) {

                Project project = projectGateway.findById(updatedActivity.getProject().getId())
                                                .orElseThrow(() -> new EntityNotFoundException(
                                                        "Project not found with ID: " + updatedActivity.getProject()
                                                                                                       .getId()));

                existingActivity.setProject(project);
            }

            existingActivity.setTitle(updatedActivity.getTitle());
            existingActivity.setDescription(updatedActivity.getDescription());
            existingActivity.setDueDate(updatedActivity.getDueDate());
            existingActivity.setAssignedTo(updatedActivity.getAssignedTo());
            existingActivity.setCompleted(updatedActivity.isCompleted());
            existingActivity.setPriority(updatedActivity.getPriority());
            existingActivity.setEstimatedHours(updatedActivity.getEstimatedHours());

            return activityGateway.save(existingActivity);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to update activity", e);
        }
    }
}