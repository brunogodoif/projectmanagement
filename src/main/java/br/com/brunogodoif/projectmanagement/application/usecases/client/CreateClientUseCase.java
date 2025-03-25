package br.com.brunogodoif.projectmanagement.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityDuplicateException;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.CreateClientInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CreateClientUseCase implements CreateClientInterface {

    private final ClientGatewayInterface clientGateway;

    public CreateClientUseCase(ClientGatewayInterface clientGateway) {
        this.clientGateway = clientGateway;
    }

    @Override
    public Client execute(ClientInputDTO clientInputDTO) {
        log.info("Creating new client: {}", clientInputDTO.getName());

        try {
            if (clientGateway.existsByEmail(clientInputDTO.getEmail())) {
                throw new EntityDuplicateException("Client with email " + clientInputDTO.getEmail() + " already exists");
            }

            Client client = new Client(
                    clientInputDTO.getId(),
                    clientInputDTO.getName(),
                    clientInputDTO.getEmail(),
                    clientInputDTO.getPhone(),
                    clientInputDTO.getCompanyName(),
                    clientInputDTO.getAddress(),
                    null,
                    null,
                    clientInputDTO.isActive()
            );

            return clientGateway.save(client);
        } catch (EntityDuplicateException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to create client", e);
        }
    }
}