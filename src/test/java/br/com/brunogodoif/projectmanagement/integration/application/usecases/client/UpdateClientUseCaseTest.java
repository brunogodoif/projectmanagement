package br.com.brunogodoif.projectmanagement.integration.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.client.UpdateClientUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityDuplicateException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateClientUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private UpdateClientUseCase updateClientUseCase;

    @Autowired
    private ClientGatewayInterface clientGateway;

    private Client existingClient;

    @BeforeEach
    void setUp() {
        // Criar um cliente para atualização
        ClientInputDTO initialClient = ClientInputDTO.builder().name("Cliente Original").email("original@teste.com")
                                                     .phone("11 1111-1111").companyName("Empresa Original")
                                                     .address("Rua Original, 100").active(true).build();

        existingClient = createClientUseCase.execute(initialClient);
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void shouldUpdateClientSuccessfully() {
        // Arrange
        ClientInputDTO updateDTO = ClientInputDTO.builder().name("Cliente Atualizado").email("atualizado@teste.com")
                                                 .phone("11 2222-2222").companyName("Empresa Atualizada")
                                                 .address("Rua Atualizada, 200").active(false).build();

        // Act
        Client updatedClient = updateClientUseCase.execute(existingClient.getId(), updateDTO);

        // Assert
        assertNotNull(updatedClient);
        assertEquals(existingClient.getId(), updatedClient.getId());
        assertEquals(updateDTO.getName(), updatedClient.getName());
        assertEquals(updateDTO.getEmail(), updatedClient.getEmail());
        assertEquals(updateDTO.getPhone(), updatedClient.getPhone());
        assertEquals(updateDTO.getCompanyName(), updatedClient.getCompanyName());
        assertEquals(updateDTO.getAddress(), updatedClient.getAddress());
        assertFalse(updatedClient.isActive());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar cliente inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        // Arrange
        ClientInputDTO updateDTO = ClientInputDTO.builder().name("Cliente Inexistente").email("inexistente@teste.com")
                                                 .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            updateClientUseCase.execute(UUID.randomUUID(), updateDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção quando novo email já existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        ClientInputDTO secondClient = ClientInputDTO.builder().name("Segundo Cliente").email("segundo@teste.com")
                                                    .phone("11 3333-3333").companyName("Empresa Segundo")
                                                    .address("Rua Segundo, 300").active(true).build();

        createClientUseCase.execute(secondClient);

        ClientInputDTO updateDTO = ClientInputDTO.builder().name("Cliente Atualizado").email("segundo@teste.com")
                                                 .build();

        // Act & Assert
        assertThrows(EntityDuplicateException.class, () -> {
            updateClientUseCase.execute(existingClient.getId(), updateDTO);
        });
    }
}