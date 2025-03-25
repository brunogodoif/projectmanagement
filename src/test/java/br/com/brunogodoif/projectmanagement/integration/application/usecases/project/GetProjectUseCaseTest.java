package br.com.brunogodoif.projectmanagement.integration.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.usecases.activity.CreateActivityUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.GetProjectUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
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

class GetProjectUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private GetProjectUseCase getProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private CreateActivityUseCase createActivityUseCase;

    private Project existingProjectWithActivities;
    private Project existingProjectWithoutActivities;

    @BeforeEach
    void setUp() {
        // Criar cliente para os projetos
        ClientInputDTO clientDTO = ClientInputDTO.builder()
                                                 .name("Cliente Projeto")
                                                 .email("cliente.projeto@teste.com")
                                                 .phone("11 98765-4321")
                                                 .companyName("Empresa Projeto")
                                                 .address("Rua Projeto, 123")
                                                 .active(true)
                                                 .build();

        Client existingClient = createClientUseCase.execute(clientDTO);

        // Criar projeto com atividades
        ProjectInputDTO projectWithActivitiesDTO = ProjectInputDTO.builder()
                                                                  .name("Projeto com Atividades")
                                                                  .description("Descrição do Projeto com Atividades")
                                                                  .clientId(existingClient.getId())
                                                                  .startDate(LocalDate.now())
                                                                  .endDate(LocalDate.now().plusMonths(3))
                                                                  .status(ProjectStatus.OPEN)
                                                                  .manager("Gerente do Projeto")
                                                                  .notes("Notas do Projeto")
                                                                  .build();

        existingProjectWithActivities = createProjectUseCase.execute(projectWithActivitiesDTO);

        // Criar atividade para o projeto
        ActivityInputDTO activityInputDTO = ActivityInputDTO.builder()
                                                            .title("Atividade do Projeto")
                                                            .description("Descrição da Atividade")
                                                            .projectId(existingProjectWithActivities.getId())
                                                            .dueDate(LocalDate.now().plusWeeks(1))
                                                            .assignedTo("Responsável")
                                                            .completed(false)
                                                            .priority("ALTA")
                                                            .estimatedHours(8)
                                                            .build();

        createActivityUseCase.execute(activityInputDTO);

        // Criar projeto sem atividades
        ProjectInputDTO projectWithoutActivitiesDTO = ProjectInputDTO.builder()
                                                                     .name("Projeto sem Atividades")
                                                                     .description("Descrição do Projeto sem Atividades")
                                                                     .clientId(existingClient.getId())
                                                                     .startDate(LocalDate.now())
                                                                     .endDate(LocalDate.now().plusMonths(3))
                                                                     .status(ProjectStatus.PLANNED)
                                                                     .manager("Gerente do Outro Projeto")
                                                                     .notes("Notas do Outro Projeto")
                                                                     .build();

        existingProjectWithoutActivities = createProjectUseCase.execute(projectWithoutActivitiesDTO);
    }

    @Test
    @DisplayName("Deve buscar projeto com atividades associadas")
    void shouldGetProjectWithActivities() {
        // Act
        Project retrievedProject = getProjectUseCase.execute(existingProjectWithActivities.getId());

        // Assert
        assertNotNull(retrievedProject);
        assertEquals(existingProjectWithActivities.getId(), retrievedProject.getId());
        assertEquals(existingProjectWithActivities.getName(), retrievedProject.getName());

        // Verificar atividades
        assertFalse(retrievedProject.getActivities().isEmpty());
        assertEquals(1, retrievedProject.getActivities().size());
    }

    @Test
    @DisplayName("Deve buscar projeto sem atividades associadas")
    void shouldGetProjectWithoutActivities() {
        // Act
        Project retrievedProject = getProjectUseCase.execute(existingProjectWithoutActivities.getId());

        // Assert
        assertNotNull(retrievedProject);
        assertEquals(existingProjectWithoutActivities.getId(), retrievedProject.getId());
        assertEquals(existingProjectWithoutActivities.getName(), retrievedProject.getName());

        // Verificar ausência de atividades
        assertTrue(retrievedProject.getActivities().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar projeto inexistente")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        UUID nonExistentProjectId = UUID.randomUUID();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            getProjectUseCase.execute(nonExistentProjectId);
        });
    }
}