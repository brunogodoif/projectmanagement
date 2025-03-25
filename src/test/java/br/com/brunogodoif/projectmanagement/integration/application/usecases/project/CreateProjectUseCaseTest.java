package br.com.brunogodoif.projectmanagement.integration.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
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

import static org.junit.jupiter.api.Assertions.*;

class CreateProjectUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    private Client savedClient;

    @BeforeEach
    void setUp() {
        // Criar um cliente para associar ao projeto
        ClientInputDTO clientDTO = ClientInputDTO.builder()
                                                 .name("Cliente Teste")
                                                 .email("cliente@teste.com")
                                                 .phone("11 98765-4321")
                                                 .companyName("Empresa Teste")
                                                 .address("Rua Teste, 123")
                                                 .active(true)
                                                 .build();

        savedClient = createClientUseCase.execute(clientDTO);
    }

    @Test
    @DisplayName("Deve criar um projeto com sucesso")
    void shouldCreateProjectSuccessfully() {
        // Arrange
        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder()
                                                         .name("Projeto Teste")
                                                         .description("Descrição do Projeto Teste")
                                                         .clientId(savedClient.getId())
                                                         .startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN)
                                                         .manager("Gerente do Projeto")
                                                         .notes("Notas do Projeto")
                                                         .build();

        // Act
        Project savedProject = createProjectUseCase.execute(projectInputDTO);

        // Assert
        assertNotNull(savedProject.getId());
        assertEquals(projectInputDTO.getName(), savedProject.getName());
        assertEquals(projectInputDTO.getDescription(), savedProject.getDescription());
        assertEquals(savedClient.getId(), savedProject.getClient().getId());
        assertEquals(projectInputDTO.getStartDate(), savedProject.getStartDate());
        assertEquals(projectInputDTO.getEndDate(), savedProject.getEndDate());
        assertEquals(projectInputDTO.getStatus(), savedProject.getStatus());
        assertEquals(projectInputDTO.getManager(), savedProject.getManager());
        assertEquals(projectInputDTO.getNotes(), savedProject.getNotes());
        assertFalse(savedProject.isDeleted());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar projeto com cliente inexistente")
    void shouldThrowExceptionWhenCreatingProjectWithNonExistentClient() {
        // Arrange
        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder()
                                                         .name("Projeto com Cliente Inexistente")
                                                         .description("Descrição do Projeto")
                                                         .clientId(java.util.UUID.randomUUID())
                                                         .startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN)
                                                         .manager("Gerente do Projeto")
                                                         .notes("Notas do Projeto")
                                                         .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            createProjectUseCase.execute(projectInputDTO);
        });
    }

    @Test
    @DisplayName("Deve criar projeto com datas de início e fim iguais")
    void shouldCreateProjectWithSameStartAndEndDate() {
        // Arrange
        LocalDate today = LocalDate.now();
        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder()
                                                         .name("Projeto de Um Dia")
                                                         .description("Projeto com data de início e fim iguais")
                                                         .clientId(savedClient.getId())
                                                         .startDate(today)
                                                         .endDate(today)
                                                         .status(ProjectStatus.OPEN)
                                                         .manager("Gerente do Projeto")
                                                         .notes("Projeto de curta duração")
                                                         .build();

        // Act
        Project savedProject = createProjectUseCase.execute(projectInputDTO);

        // Assert
        assertEquals(today, savedProject.getStartDate());
        assertEquals(today, savedProject.getEndDate());
    }
}