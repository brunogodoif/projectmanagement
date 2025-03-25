package br.com.brunogodoif.projectmanagement.integration.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.CreateActivityUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.UpdateActivityUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdateActivityUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateActivityUseCase createActivityUseCase;

    @Autowired
    private UpdateActivityUseCase updateActivityUseCase;

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    private Activity existingActivity;
    private Project existingProject;
    private Project anotherProject;

    @BeforeEach
    void setUp() {
        // Criar cliente para projetos
        ClientInputDTO clientDTO = ClientInputDTO.builder()
                                                 .name("Cliente Projeto")
                                                 .email("cliente.projeto@teste.com")
                                                 .phone("11 98765-4321")
                                                 .companyName("Empresa Projeto")
                                                 .address("Rua Projeto, 123")
                                                 .active(true)
                                                 .build();

        Client savedClient = createClientUseCase.execute(clientDTO);

        // Criar primeiro projeto
        ProjectInputDTO firstProjectDTO = ProjectInputDTO.builder()
                                                         .name("Projeto Primeiro")
                                                         .description("Descrição do Primeiro Projeto")
                                                         .clientId(savedClient.getId())
                                                         .startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN)
                                                         .manager("Gerente do Primeiro Projeto")
                                                         .notes("Notas do Primeiro Projeto")
                                                         .build();

        existingProject = createProjectUseCase.execute(firstProjectDTO);

        // Criar segundo projeto
        ProjectInputDTO secondProjectDTO = ProjectInputDTO.builder()
                                                          .name("Projeto Segundo")
                                                          .description("Descrição do Segundo Projeto")
                                                          .clientId(savedClient.getId())
                                                          .startDate(LocalDate.now())
                                                          .endDate(LocalDate.now().plusMonths(3))
                                                          .status(ProjectStatus.IN_PROGRESS)
                                                          .manager("Gerente do Segundo Projeto")
                                                          .notes("Notas do Segundo Projeto")
                                                          .build();

        anotherProject = createProjectUseCase.execute(secondProjectDTO);

        // Criar atividade inicial
        ActivityInputDTO activityDTO = ActivityInputDTO.builder()
                                                       .title("Atividade Original")
                                                       .description("Descrição da Atividade Original")
                                                       .projectId(existingProject.getId())
                                                       .dueDate(LocalDate.now().plusWeeks(2))
                                                       .assignedTo("Desenvolvedor Original")
                                                       .completed(false)
                                                       .priority("ALTA")
                                                       .estimatedHours(8)
                                                       .build();

        existingActivity = createActivityUseCase.execute(activityDTO);
    }

    @Test
    @DisplayName("Deve atualizar uma atividade com sucesso")
    void shouldUpdateActivitySuccessfully() {
        // Arrange
        ActivityInputDTO updateDTO = ActivityInputDTO.builder()
                                                     .title("Atividade Atualizada")
                                                     .description("Descrição Atualizada")
                                                     .projectId(existingProject.getId())
                                                     .dueDate(LocalDate.now().plusWeeks(4))
                                                     .assignedTo("Desenvolvedor Atualizado")
                                                     .completed(true)
                                                     .priority("BAIXA")
                                                     .estimatedHours(4)
                                                     .build();

        // Act
        Activity updatedActivity = updateActivityUseCase.execute(existingActivity.getId(), updateDTO);

        // Assert
        assertNotNull(updatedActivity);
        assertEquals(existingActivity.getId(), updatedActivity.getId());
        assertEquals(updateDTO.getTitle(), updatedActivity.getTitle());
        assertEquals(updateDTO.getDescription(), updatedActivity.getDescription());
        assertEquals(updateDTO.getDueDate(), updatedActivity.getDueDate());
        assertEquals(updateDTO.getAssignedTo(), updatedActivity.getAssignedTo());
        assertEquals(updateDTO.isCompleted(), updatedActivity.isCompleted());
        assertEquals(updateDTO.getPriority(), updatedActivity.getPriority());
        assertEquals(updateDTO.getEstimatedHours(), updatedActivity.getEstimatedHours());
    }

    @Test
    @DisplayName("Deve atualizar atividade para outro projeto")
    void shouldUpdateActivityToAnotherProject() {
        // Arrange
        ActivityInputDTO updateDTO = ActivityInputDTO.builder()
                                                     .title("Atividade em Outro Projeto")
                                                     .description("Descrição da Atividade em Outro Projeto")
                                                     .projectId(anotherProject.getId())
                                                     .dueDate(LocalDate.now().plusWeeks(4))
                                                     .assignedTo("Desenvolvedor Outro Projeto")
                                                     .completed(false)
                                                     .priority("MÉDIA")
                                                     .estimatedHours(6)
                                                     .build();

        // Act
        Activity updatedActivity = updateActivityUseCase.execute(existingActivity.getId(), updateDTO);

        // Assert
        assertNotNull(updatedActivity);
        assertEquals(existingActivity.getId(), updatedActivity.getId());
        assertEquals(anotherProject.getId(), updatedActivity.getProject().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar atividade inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentActivity() {
        // Arrange
        ActivityInputDTO updateDTO = ActivityInputDTO.builder()
                                                     .title("Atividade Inexistente")
                                                     .description("Descrição da Atividade Inexistente")
                                                     .projectId(existingProject.getId())
                                                     .dueDate(LocalDate.now().plusWeeks(2))
                                                     .assignedTo("Desenvolvedor Inexistente")
                                                     .completed(false)
                                                     .priority("MÉDIA")
                                                     .estimatedHours(4)
                                                     .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            updateActivityUseCase.execute(UUID.randomUUID(), updateDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com projeto inexistente")
    void shouldThrowExceptionWhenUpdatingWithNonExistentProject() {
        // Arrange
        ActivityInputDTO updateDTO = ActivityInputDTO.builder()
                                                     .title("Atividade com Projeto Inexistente")
                                                     .description("Descrição da Atividade")
                                                     .projectId(UUID.randomUUID())
                                                     .dueDate(LocalDate.now().plusWeeks(2))
                                                     .assignedTo("Desenvolvedor Teste")
                                                     .completed(false)
                                                     .priority("MÉDIA")
                                                     .estimatedHours(4)
                                                     .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            updateActivityUseCase.execute(existingActivity.getId(), updateDTO);
        });
    }
}