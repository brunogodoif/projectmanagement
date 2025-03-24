package br.com.brunogodoif.projectmanagement.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.CreateProjectInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2

public class CreateProjectUseCase implements CreateProjectInterface {

    private final ProjectGatewayInterface projectGateway;
    private final ClientGatewayInterface clientGateway;

    public CreateProjectUseCase(ProjectGatewayInterface projectGateway, ClientGatewayInterface clientGateway) {
        this.projectGateway = projectGateway;
        this.clientGateway = clientGateway;
    }

    @Override
    public Project execute(Project project) {
        log.info("Creating new project: {}", project.getName());

        try {
            Client client = clientGateway.findById(project.getClient().getId())
                                         .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + project.getClient()
                                                                                                                              .getId()));

            project.setClient(client);

            return projectGateway.save(project);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to create project", e);
        }
    }
}