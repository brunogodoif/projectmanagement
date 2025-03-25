package br.com.brunogodoif.projectmanagement.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
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
public class DeleteClientUseCase implements DeleteEntityInterface<UUID> {

    private final ClientGatewayInterface clientGateway;
    private final ProjectGatewayInterface projectGateway;

    public DeleteClientUseCase(ClientGatewayInterface clientGateway, ProjectGatewayInterface projectGateway) {
        this.clientGateway = clientGateway;
        this.projectGateway = projectGateway;
    }

    @Override
    public void execute(UUID id) {
        log.info("Deleting client with ID: {}", id);

        try {
            clientGateway.findById(id)
                                         .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + id));

            List<Project> projects = projectGateway.findByClientId(id);
            if (!projects.isEmpty()) {
                throw new EntityInUseException("Client with ID " + id + " cannot be deleted because it has " + projects.size() + " associated project(s)");
            }
            clientGateway.deleteById(id);
            log.info("Client with ID: {} successfully deleted", id);
        } catch (EntityNotFoundException | EntityInUseException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to delete client", e);
        }
    }
}