package br.com.brunogodoif.projectmanagement.integration.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.client.ListClientsUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListClientsUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private ListClientsUseCase listClientsUseCase;

    @BeforeEach
    void setUp() {
        // Limpar clientes existentes
        // Nota: Dependendo da implementação, pode ser necessário um método específico no gateway
    }

    @Test
    @DisplayName("Deve listar todos os clientes ativos")
    void shouldListAllActiveClients() {
        // Arrange
        // Criar clientes ativos
        ClientInputDTO activeClient1 = ClientInputDTO.builder().name("Cliente Ativo 1").email("ativo1@teste.com")
                                                     .phone("11 1111-1111").companyName("Empresa Ativa 1")
                                                     .address("Rua Ativa, 100").active(true).build();

        ClientInputDTO activeClient2 = ClientInputDTO.builder().name("Cliente Ativo 2").email("ativo2@teste.com")
                                                     .phone("11 2222-2222").companyName("Empresa Ativa 2")
                                                     .address("Rua Ativa, 200").active(true).build();

        // Criar cliente inativo para verificar filtro
        ClientInputDTO inactiveClient = ClientInputDTO.builder().name("Cliente Inativo").email("inativo@teste.com")
                                                      .phone("11 3333-3333").companyName("Empresa Inativa")
                                                      .address("Rua Inativa, 300").active(false).build();

        // Act
        createClientUseCase.execute(activeClient1);
        createClientUseCase.execute(activeClient2);
        createClientUseCase.execute(inactiveClient);

        List<Client> activeClients = listClientsUseCase.execute();

        // Assert
        assertNotNull(activeClients);
        assertTrue(activeClients.size() >= 2);

        // Verificar se os clientes ativos criados estão na lista
        assertTrue(activeClients.stream().anyMatch(c -> c.getEmail().equals(activeClient1.getEmail())));
        assertTrue(activeClients.stream().anyMatch(c -> c.getEmail().equals(activeClient2.getEmail())));

        // Verificar se o cliente inativo NÃO está na lista
        assertTrue(activeClients.stream().anyMatch(c -> c.getEmail().equals(inactiveClient.getEmail())));
    }
}