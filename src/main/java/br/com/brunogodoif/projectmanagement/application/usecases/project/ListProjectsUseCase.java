package br.com.brunogodoif.projectmanagement.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.ListProjectsInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class ListProjectsUseCase implements ListProjectsInterface {

    private final ProjectGatewayInterface projectGateway;

    public ListProjectsUseCase(ProjectGatewayInterface projectGateway) {
        this.projectGateway = projectGateway;
    }

    @Override
    public List<Project> execute() {
        log.info("Listing all active projects");
        return projectGateway.findAllActive();
    }

    @Override
    public List<Project> executeByStatus(ProjectStatus status) {
        log.info("Listing all active projects with status: {}", status);
        return projectGateway.findByStatus(status);
    }

    @Override
    public List<Project> executeByClient(UUID clientId) {
        log.info("Listing all active projects for client: {}", clientId);
        return projectGateway.findByClientId(clientId);
    }
}