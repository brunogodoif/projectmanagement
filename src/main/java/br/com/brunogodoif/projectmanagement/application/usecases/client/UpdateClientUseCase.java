package br.com.brunogodoif.projectmanagement.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityDuplicateException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.UpdateClientInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Log4j2
public class UpdateClientUseCase implements UpdateClientInterface {

    private final ClientGatewayInterface clientGateway;

    public UpdateClientUseCase(ClientGatewayInterface clientGateway) {
        this.clientGateway = clientGateway;
    }

    @Override
    public Client execute(UUID id, ClientInputDTO clientInputDTO) {
        log.info("Updating client with ID: {}", id);

        try {
            Client existingClient = clientGateway.findById(id).orElseThrow(() -> new EntityNotFoundException(
                    "Client not found with ID: " + id));

            if (!existingClient.getEmail()
                               .equals(clientInputDTO.getEmail()) && clientGateway.existsByEmail(clientInputDTO.getEmail())) {
                throw new EntityDuplicateException("Client with email " + clientInputDTO.getEmail() + " already exists");
            }

            Client newClient = new Client(
                    existingClient.getId(),
                    clientInputDTO.getName(),
                    clientInputDTO.getEmail(),
                    clientInputDTO.getPhone(),
                    clientInputDTO.getCompanyName(),
                    clientInputDTO.getAddress(),
                    existingClient.getCreatedAt(),
                    LocalDateTime.now(),
                    clientInputDTO.isActive()
            );

            return clientGateway.save(newClient);
        } catch (EntityNotFoundException | EntityDuplicateException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to update client", e);
        }
    }
}