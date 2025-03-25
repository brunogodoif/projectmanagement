package br.com.brunogodoif.projectmanagement.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.GetClientInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class GetClientUseCase implements GetClientInterface {

    private final ClientGatewayInterface clientGateway;
    private final ProjectGatewayInterface projectGateway;

    public GetClientUseCase(ClientGatewayInterface clientGateway, ProjectGatewayInterface projectGateway) {
        this.clientGateway = clientGateway;
        this.projectGateway = projectGateway;
    }

    @Override
    public Client execute(UUID id) {
        log.info("Getting client with ID: {}", id);

        Client client = clientGateway.findById(id)
                                     .orElseThrow(() -> new EntityNotFoundException("Client not found with ID: " + id));

        List<Project> clientProjects = projectGateway.findByClientId(id);

        Client clientWithProjects = new Client(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getPhone(),
                client.getCompanyName(),
                client.getAddress(),
                client.getCreatedAt(),
                client.getUpdatedAt(),
                client.isActive()
        );

        for (Project project : clientProjects) {
            clientWithProjects.addProject(project);
        }

        return clientWithProjects;
    }
}