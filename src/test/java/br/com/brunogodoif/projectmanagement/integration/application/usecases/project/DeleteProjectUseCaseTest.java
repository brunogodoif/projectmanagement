package br.com.brunogodoif.projectmanagement.integration.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.CreateActivityUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.DeleteProjectUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityInUseException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeleteProjectUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private DeleteProjectUseCase deleteProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private CreateActivityUseCase createActivityUseCase;

    @Autowired
    private ProjectGatewayInterface projectGateway;

    private Project existingProject;
    private Client existingClient;

    @BeforeEach
    void setUp() {
        // Criar cliente para o projeto
        ClientInputDTO clientDTO = ClientInputDTO.builder()
                                                 .name("Cliente Projeto")
                                                 .email("cliente.projeto@teste.com")
                                                 .phone("11 98765-4321")
                                                 .companyName("Empresa Projeto")
                                                 .address("Rua Projeto, 123")
                                                 .active(true)
                                                 .build();

        existingClient = createClientUseCase.execute(clientDTO);

        // Criar projeto
        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder()
                                                         .name("Projeto para Exclusão")
                                                         .description("Descrição do Projeto para Exclusão")
                                                         .clientId(existingClient.getId())
                                                         .startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN)
                                                         .manager("Gerente do Projeto")
                                                         .notes("Notas do Projeto")
                                                         .build();

        existingProject = createProjectUseCase.execute(projectInputDTO);
    }

    @Test
    @DisplayName("Deve excluir um projeto com sucesso")
    void shouldDeleteProjectSuccessfully() {
        // Act
        deleteProjectUseCase.execute(existingProject.getId());

        // Assert
        assertTrue(projectGateway.findById(existingProject.getId()).isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir projeto inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentProject() {
        // Arrange
        UUID nonExistentProjectId = UUID.randomUUID();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            deleteProjectUseCase.execute(nonExistentProjectId);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir projeto com atividades associadas")
    void shouldThrowExceptionWhenDeletingProjectWithActivities() {
        // Arrange
        ActivityInputDTO activityInputDTO = ActivityInputDTO.builder()
                                                            .title("Atividade do Projeto")
                                                            .description("Descrição da Atividade")
                                                            .projectId(existingProject.getId())
                                                            .dueDate(LocalDate.now().plusWeeks(1))
                                                            .assignedTo("Responsável")
                                                            .completed(false)
                                                            .priority("ALTA")
                                                            .estimatedHours(8)
                                                            .build();

        createActivityUseCase.execute(activityInputDTO);

        // Act & Assert
        assertThrows(EntityInUseException.class, () -> {
            deleteProjectUseCase.execute(existingProject.getId());
        });
    }
}