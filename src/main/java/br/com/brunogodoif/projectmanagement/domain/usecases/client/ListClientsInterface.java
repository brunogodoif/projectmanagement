package br.com.brunogodoif.projectmanagement.domain.usecases.client;

import br.com.brunogodoif.projectmanagement.domain.entities.Client;

import java.util.List;

public interface ListClientsInterface {
    List<Client> execute();
}