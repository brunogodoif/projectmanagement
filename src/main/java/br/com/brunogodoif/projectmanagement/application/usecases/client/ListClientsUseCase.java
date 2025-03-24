package br.com.brunogodoif.projectmanagement.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.ListClientsInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class ListClientsUseCase implements ListClientsInterface {

    private final ClientGatewayInterface clientGateway;

    public ListClientsUseCase(ClientGatewayInterface clientGateway) {
        this.clientGateway = clientGateway;
    }

    @Override
    public List<Client> execute() {
        log.info("Listing all active clients");
        return clientGateway.findAllActive();
    }
}
