package br.com.brunogodoif.projectmanagement.domain.usecases.client;

import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;

public interface CreateClientInterface {
    Client execute(ClientInputDTO client);
}