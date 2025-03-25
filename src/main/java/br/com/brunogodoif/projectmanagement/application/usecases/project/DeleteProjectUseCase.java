package br.com.brunogodoif.projectmanagement.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityInUseException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.DeleteEntityInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class DeleteProjectUseCase implements DeleteEntityInterface<UUID> {

    private final ProjectGatewayInterface projectGateway;
    private final ActivityGatewayInterface activityGateway;

    public DeleteProjectUseCase(ProjectGatewayInterface projectGateway, ActivityGatewayInterface activityGateway) {
        this.projectGateway = projectGateway;
        this.activityGateway = activityGateway;
    }

    @Override
    public void execute(UUID id) {
        log.info("Deleting project with ID: {}", id);

        try {
            projectGateway.findById(id)
                          .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));

            List<Activity> activities = activityGateway.findByProjectId(id);
            if (!activities.isEmpty()) {
                throw new EntityInUseException("Project with ID " + id + " cannot be deleted because it has " + activities.size() + " associated activity(ies)");
            }

            projectGateway.deleteById(id);
            log.info("Project with ID: {} successfully deleted", id);
        } catch (EntityNotFoundException | EntityInUseException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to delete project", e);
        }
    }
}