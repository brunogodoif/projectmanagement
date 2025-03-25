package br.com.brunogodoif.projectmanagement.integration.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityDuplicateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class CreateClientUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private ClientGatewayInterface clientGateway;

    @Test
    @DisplayName("Deve criar um cliente com sucesso no banco de dados")
    void shouldCreateClientSuccessfully() {
        // Arrange
        ClientInputDTO clientInputDTO = ClientInputDTO.builder().name("Empresa Teste Integração")
                                                      .email("integracaotest@empresa.com.br").phone("11 99999-9999")
                                                      .companyName("Empresa Teste Integração Ltda")
                                                      .address("Rua de Teste, 123, São Paulo, SP").active(true).build();

        // Act
        Client savedClient = createClientUseCase.execute(clientInputDTO);

        // Assert
        assertNotNull(savedClient.getId());
        assertEquals(clientInputDTO.getName(), savedClient.getName());
        assertEquals(clientInputDTO.getEmail(), savedClient.getEmail());
        assertTrue(clientGateway.findById(savedClient.getId()).isPresent());
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        ClientInputDTO firstClient = ClientInputDTO.builder().name("Primeiro Cliente").email("primeiro@teste.com")
                                                   .phone("11 1111-1111").companyName("Primeira Empresa")
                                                   .address("Rua Primeira, 100").active(true).build();

        // Salva o primeiro cliente
        createClientUseCase.execute(firstClient);

        // Tenta salvar cliente com mesmo email
        ClientInputDTO duplicateClient = ClientInputDTO.builder().name("Cliente Duplicado").email("primeiro@teste.com")
                                                       .phone("11 2222-2222").companyName("Segunda Empresa")
                                                       .address("Rua Segunda, 200").active(true).build();

        // Act & Assert
        assertThrows(EntityDuplicateException.class, () -> {
            createClientUseCase.execute(duplicateClient);
        });
    }
}



