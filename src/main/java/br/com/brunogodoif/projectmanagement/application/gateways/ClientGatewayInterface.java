package br.com.brunogodoif.projectmanagement.application.gateways;

import br.com.brunogodoif.projectmanagement.domain.entities.Client;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientGatewayInterface {
    Client save(Client client);

    Optional<Client> findById(UUID id);

    List<Client> findAll();

    List<Client> findAllActive();

    void deleteById(UUID id);

    boolean existsByEmail(String email);
}