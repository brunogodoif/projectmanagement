package br.com.brunogodoif.projectmanagement.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityDuplicateException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.UpdateClientInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class UpdateClientUseCase implements UpdateClientInterface {

    private final ClientGatewayInterface clientGateway;

    public UpdateClientUseCase(ClientGatewayInterface clientGateway) {
        this.clientGateway = clientGateway;
    }

    @Override
    public Client execute(UUID id, Client updatedClient) {
        log.info("Updating client with ID: {}", id);

        try {
            Client existingClient = clientGateway.findById(id).orElseThrow(() -> new EntityNotFoundException(
                    "Client not found with ID: " + id));

            if (!existingClient.getEmail()
                               .equals(updatedClient.getEmail()) && clientGateway.existsByEmail(updatedClient.getEmail())) {
                throw new EntityDuplicateException("Client with email " + updatedClient.getEmail() + " already exists");
            }

            existingClient.setName(updatedClient.getName());
            existingClient.setEmail(updatedClient.getEmail());
            existingClient.setPhone(updatedClient.getPhone());
            existingClient.setCompanyName(updatedClient.getCompanyName());
            existingClient.setAddress(updatedClient.getAddress());

            return clientGateway.save(existingClient);
        } catch (EntityNotFoundException | EntityDuplicateException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to update client", e);
        }
    }
}