package br.com.brunogodoif.projectmanagement.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
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
    public Project execute(ProjectInputDTO projectInputDTO) {
        log.info("Creating new project: {}", projectInputDTO.getName());

        try {
            Client client = clientGateway.findById(projectInputDTO.getClientId())
                                         .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + projectInputDTO.getClientId()));

            Project project = new Project(
                    projectInputDTO.getId(),
                    projectInputDTO.getName(),
                    projectInputDTO.getDescription(),
                    client,
                    projectInputDTO.getStartDate(),
                    projectInputDTO.getEndDate(),
                    projectInputDTO.getStatus(),
                    projectInputDTO.getManager(),
                    projectInputDTO.getNotes(),
                    false,
                    null,
                    null
            );

            return projectGateway.save(project);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to create project", e);
        }
    }
}