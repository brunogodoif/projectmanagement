package br.com.brunogodoif.projectmanagement.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.UpdateProjectInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

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
    public Project execute(UUID id, Project updatedProject) {
        log.info("Updating project with ID: {}", id);

        try {
            Project existingProject = projectGateway.findById(id).orElseThrow(() -> new EntityNotFoundException(
                    "Project not found with ID: " + id));

            if (updatedProject.getClient() != null && !existingProject.getClient().getId()
                                                                      .equals(updatedProject.getClient().getId())) {

                Client client = clientGateway.findById(updatedProject.getClient().getId())
                                             .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + updatedProject.getClient()
                                                                                                                                         .getId()));

                existingProject.setClient(client);
            }

            existingProject.setName(updatedProject.getName());
            existingProject.setDescription(updatedProject.getDescription());
            existingProject.setStartDate(updatedProject.getStartDate());
            existingProject.setEndDate(updatedProject.getEndDate());
            existingProject.setStatus(updatedProject.getStatus());
            existingProject.setManager(updatedProject.getManager());
            existingProject.setNotes(updatedProject.getNotes());

            return projectGateway.save(existingProject);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to update project", e);
        }
    }
}