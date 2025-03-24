package br.com.brunogodoif.projectmanagement.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.ListActivitiesByProjectInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class ListActivitiesByProjectUseCase implements ListActivitiesByProjectInterface {

    private final ActivityGatewayInterface activityGateway;
    private final ProjectGatewayInterface projectGateway;

    public ListActivitiesByProjectUseCase(ActivityGatewayInterface activityGateway,
                                          ProjectGatewayInterface projectGateway
                                         ) {
        this.activityGateway = activityGateway;
        this.projectGateway = projectGateway;
    }

    @Override
    public List<Activity> execute(UUID projectId) {
        log.info("Listing all activities for project: {}", projectId);

        projectGateway.findById(projectId)
                      .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        return activityGateway.findByProjectId(projectId);
    }
}
