package br.com.brunogodoif.projectmanagement.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.UpdateProjectInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Log4j2
public class UpdateProjectUseCase implements UpdateProjectInterface {

    private final ProjectGatewayInterface projectGateway;
    private final ClientGatewayInterface clientGateway;

    public UpdateProjectUseCase(ProjectGatewayInterface projectGateway, ClientGatewayInterface clientGateway) {
        this.projectGateway = projectGateway;
        this.clientGateway = clientGateway;
    }

    @Override
    public Project execute(UUID id, ProjectInputDTO projectInputDTO) {
        log.info("Updating project with ID: {}", id);

        try {
            Project existingProject = projectGateway.findById(id).orElseThrow(() -> new EntityNotFoundException(
                    "Project not found with ID: " + id));

            Client client;

            if (!existingProject.getClient().getId().equals(projectInputDTO.getClientId())) {
                client = clientGateway.findById(projectInputDTO.getClientId())
                                      .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + projectInputDTO.getClientId()));
            } else {
                client = existingProject.getClient();
            }

            Project updatedProject = new Project(existingProject.getId(),
                                                 projectInputDTO.getName(),
                                                 projectInputDTO.getDescription(),
                                                 client,
                                                 projectInputDTO.getStartDate(),
                                                 projectInputDTO.getEndDate(),
                                                 projectInputDTO.getStatus(),
                                                 projectInputDTO.getManager(),
                                                 projectInputDTO.getNotes(),
                                                 existingProject.isDeleted(),
                                                 existingProject.getCreatedAt(),
                                                 LocalDateTime.now());

            return projectGateway.save(updatedProject);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to update project", e);
        }
    }
}