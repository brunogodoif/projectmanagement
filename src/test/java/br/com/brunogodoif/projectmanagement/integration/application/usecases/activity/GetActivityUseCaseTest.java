package br.com.brunogodoif.projectmanagement.integration.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.CreateActivityUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.GetActivityUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetActivityUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateActivityUseCase createActivityUseCase;

    @Autowired
    private GetActivityUseCase getActivityUseCase;

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    private Activity existingActivity;

    @BeforeEach
    void setUp() {
        // Criar cliente
        ClientInputDTO clientDTO = ClientInputDTO.builder()
                                                 .name("Cliente Projeto")
                                                 .email("cliente.projeto@teste.com")
                                                 .phone("11 98765-4321")
                                                 .companyName("Empresa Projeto")
                                                 .address("Rua Projeto, 123")
                                                 .active(true)
                                                 .build();

        Client savedClient = createClientUseCase.execute(clientDTO);

        // Criar projeto
        ProjectInputDTO projectDTO = ProjectInputDTO.builder()
                                                    .name("Projeto para Atividade")
                                                    .description("Descrição do Projeto")
                                                    .clientId(savedClient.getId())
                                                    .startDate(LocalDate.now())
                                                    .endDate(LocalDate.now().plusMonths(3))
                                                    .status(ProjectStatus.OPEN)
                                                    .manager("Gerente do Projeto")
                                                    .notes("Notas do Projeto")
                                                    .build();

        Project savedProject = createProjectUseCase.execute(projectDTO);

        // Criar atividade
        ActivityInputDTO activityDTO = ActivityInputDTO.builder()
                                                       .title("Atividade para Busca")
                                                       .description("Descrição da Atividade")
                                                       .projectId(savedProject.getId())
                                                       .dueDate(LocalDate.now().plusWeeks(2))
                                                       .assignedTo("Desenvolvedor Teste")
                                                       .completed(false)
                                                       .priority("ALTA")
                                                       .estimatedHours(8)
                                                       .build();

        existingActivity = createActivityUseCase.execute(activityDTO);
    }

    @Test
    @DisplayName("Deve buscar uma atividade por ID com sucesso")
    void shouldGetActivityByIdSuccessfully() {
        // Act
        Activity retrievedActivity = getActivityUseCase.execute(existingActivity.getId());

        // Assert
        assertNotNull(retrievedActivity);
        assertEquals(existingActivity.getId(), retrievedActivity.getId());
        assertEquals(existingActivity.getTitle(), retrievedActivity.getTitle());
        assertEquals(existingActivity.getDescription(), retrievedActivity.getDescription());
        assertEquals(existingActivity.getProject().getId(), retrievedActivity.getProject().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar atividade inexistente")
    void shouldThrowExceptionWhenActivityNotFound() {
        // Arrange
        UUID nonExistentActivityId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            getActivityUseCase.execute(nonExistentActivityId);
        });

        assertEquals("Activity not found with ID: " + nonExistentActivityId, exception.getMessage());
    }
}