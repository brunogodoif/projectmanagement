package br.com.brunogodoif.projectmanagement.domain.usecases.client;

import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;

import java.util.UUID;

public interface UpdateClientInterface {
    Client execute(UUID id, ClientInputDTO client);
}