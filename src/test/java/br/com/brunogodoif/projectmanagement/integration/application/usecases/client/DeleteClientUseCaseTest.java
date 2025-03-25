package br.com.brunogodoif.projectmanagement.integration.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.client.DeleteClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityInUseException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeleteClientUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private DeleteClientUseCase deleteClientUseCase;

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private ClientGatewayInterface clientGateway;

    private Client existingClient;

    @BeforeEach
    void setUp() {
        // Criar um cliente para testes
        ClientInputDTO clientInputDTO = ClientInputDTO.builder().name("Cliente para Exclusão")
                                                      .email("exclusao@teste.com").phone("11 1111-1111")
                                                      .companyName("Empresa para Exclusão")
                                                      .address("Rua de Exclusão, 100").active(true).build();

        existingClient = createClientUseCase.execute(clientInputDTO);
    }

    @Test
    @DisplayName("Deve excluir um cliente com sucesso")
    void shouldDeleteClientSuccessfully() {
        // Act
        deleteClientUseCase.execute(existingClient.getId());

        // Assert
        assertTrue(clientGateway.findById(existingClient.getId()).isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir cliente inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentClient() {
        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            deleteClientUseCase.execute(UUID.randomUUID());
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir cliente com projetos associados")
    void shouldThrowExceptionWhenDeletingClientWithProjects() {
        // Arrange
        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder().name("Projeto do Cliente")
                                                         .description("Descrição do Projeto")
                                                         .clientId(existingClient.getId()).startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN).manager("Gerente do Projeto")
                                                         .notes("Notas do Projeto").build();

        createProjectUseCase.execute(projectInputDTO);

        // Act & Assert
        assertThrows(EntityInUseException.class, () -> {
            deleteClientUseCase.execute(existingClient.getId());
        });
    }
}