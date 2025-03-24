package br.com.brunogodoif.projectmanagement.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.GetProjectInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class GetProjectUseCase implements GetProjectInterface {

    private final ProjectGatewayInterface projectGateway;
    private final ActivityGatewayInterface activityGateway;

    public GetProjectUseCase(ProjectGatewayInterface projectGateway, ActivityGatewayInterface activityGateway) {
        this.projectGateway = projectGateway;
        this.activityGateway = activityGateway;
    }

    @Override
    public Project execute(UUID id) {
        log.info("Getting project with ID: {}", id);

        Project project = projectGateway.findById(id)
                                        .orElseThrow(() -> new EntityNotFoundException("Project not found with ID: " + id));

        List<Activity> activities = activityGateway.findByProjectId(id);

        project.setActivities(activities);

        log.info("Found project with ID: {} with {} activities", id, activities.size());

        return project;
    }
}