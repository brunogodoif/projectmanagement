package br.com.brunogodoif.projectmanagement.domain.usecases.client;

import br.com.brunogodoif.projectmanagement.domain.entities.Client;

import java.util.UUID;

public interface UpdateClientInterface {
    Client execute(UUID id, Client client);
}