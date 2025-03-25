package br.com.brunogodoif.projectmanagement.integration.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.client.GetClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class GetClientUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private GetClientUseCase getClientUseCase;

    private Client clientWithProjects;
    private Client clientWithoutProjects;

    @BeforeEach
    void setUp() {
        // Criar cliente com projetos
        ClientInputDTO clientWithProjectsDTO = ClientInputDTO.builder().name("Cliente com Projetos")
                                                             .email("clienteprojetos@teste.com").phone("11 1111-1111")
                                                             .companyName("Empresa com Projetos")
                                                             .address("Rua dos Projetos, 100").active(true).build();

        clientWithProjects = createClientUseCase.execute(clientWithProjectsDTO);

        // Criar projeto para o cliente
        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder().name("Projeto do Cliente")
                                                         .description("Descrição do Projeto")
                                                         .clientId(clientWithProjects.getId())
                                                         .startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN).manager("Gerente do Projeto")
                                                         .notes("Notas do Projeto").build();

        createProjectUseCase.execute(projectInputDTO);

        // Criar cliente sem projetos
        ClientInputDTO clientWithoutProjectsDTO = ClientInputDTO.builder().name("Cliente sem Projetos")
                                                                .email("clientesemprojetos@teste.com")
                                                                .phone("11 2222-2222")
                                                                .companyName("Empresa sem Projetos")
                                                                .address("Rua sem Projetos, 200").active(true).build();

        clientWithoutProjects = createClientUseCase.execute(clientWithoutProjectsDTO);
    }

    @Test
    @DisplayName("Deve buscar cliente com projetos associados")
    void shouldGetClientWithProjects() {
        // Act
        Client retrievedClient = getClientUseCase.execute(clientWithProjects.getId());

        // Assert
        assertNotNull(retrievedClient);
        assertEquals(clientWithProjects.getId(), retrievedClient.getId());
        assertEquals(clientWithProjects.getName(), retrievedClient.getName());

        // Verificar projetos associados
        assertNotNull(retrievedClient.getProjects());
        assertFalse(retrievedClient.getProjects().isEmpty());

        // Verificar detalhes do projeto
        Project retrievedProject = retrievedClient.getProjects().get(0);
        assertNotNull(retrievedProject);
        assertEquals(ProjectStatus.OPEN, retrievedProject.getStatus());
    }

    @Test
    @DisplayName("Deve buscar cliente sem projetos associados")
    void shouldGetClientWithoutProjects() {
        // Act
        Client retrievedClient = getClientUseCase.execute(clientWithoutProjects.getId());

        // Assert
        assertNotNull(retrievedClient);
        assertEquals(clientWithoutProjects.getId(), retrievedClient.getId());
        assertEquals(clientWithoutProjects.getName(), retrievedClient.getName());

        // Verificar que não há projetos
        assertTrue(retrievedClient.getProjects().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar cliente inexistente")
    void shouldThrowExceptionWhenClientNotFound() {
        // Arrange
        UUID nonExistentClientId = UUID.randomUUID();

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            getClientUseCase.execute(nonExistentClientId);
        });

        // Verificar mensagem de erro
        assertEquals("Client not found with ID: " + nonExistentClientId, exception.getMessage());
    }
}