package br.com.brunogodoif.projectmanagement.integration.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.CreateActivityUseCase;
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

import static org.junit.jupiter.api.Assertions.*;

class CreateActivityUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateActivityUseCase createActivityUseCase;

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    private Project savedProject;

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

        savedProject = createProjectUseCase.execute(projectDTO);
    }

    @Test
    @DisplayName("Deve criar uma atividade com sucesso")
    void shouldCreateActivitySuccessfully() {
        // Arrange
        ActivityInputDTO activityDTO = ActivityInputDTO.builder()
                                                       .title("Implementar Funcionalidade X")
                                                       .description("Descrição detalhada da funcionalidade")
                                                       .projectId(savedProject.getId())
                                                       .dueDate(LocalDate.now().plusWeeks(2))
                                                       .assignedTo("Desenvolvedor Teste")
                                                       .completed(false)
                                                       .priority("ALTA")
                                                       .estimatedHours(8)
                                                       .build();

        // Act
        Activity savedActivity = createActivityUseCase.execute(activityDTO);

        // Assert
        assertNotNull(savedActivity.getId());
        assertEquals(activityDTO.getTitle(), savedActivity.getTitle());
        assertEquals(activityDTO.getDescription(), savedActivity.getDescription());
        assertEquals(savedProject.getId(), savedActivity.getProject().getId());
        assertEquals(activityDTO.getDueDate(), savedActivity.getDueDate());
        assertEquals(activityDTO.getAssignedTo(), savedActivity.getAssignedTo());
        assertEquals(activityDTO.isCompleted(), savedActivity.isCompleted());
        assertEquals(activityDTO.getPriority(), savedActivity.getPriority());
        assertEquals(activityDTO.getEstimatedHours(), savedActivity.getEstimatedHours());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar atividade com projeto inexistente")
    void shouldThrowExceptionWhenCreatingActivityWithNonExistentProject() {
        // Arrange
        ActivityInputDTO activityDTO = ActivityInputDTO.builder()
                                                       .title("Atividade com Projeto Inexistente")
                                                       .description("Descrição da atividade")
                                                       .projectId(java.util.UUID.randomUUID())
                                                       .dueDate(LocalDate.now().plusWeeks(2))
                                                       .assignedTo("Desenvolvedor Teste")
                                                       .completed(false)
                                                       .priority("MÉDIA")
                                                       .estimatedHours(4)
                                                       .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            createActivityUseCase.execute(activityDTO);
        });
    }

    @Test
    @DisplayName("Deve criar atividade com data de vencimento no passado")
    void shouldCreateActivityWithPastDueDate() {
        // Arrange
        ActivityInputDTO activityDTO = ActivityInputDTO.builder()
                                                       .title("Atividade Atrasada")
                                                       .description("Descrição de atividade atrasada")
                                                       .projectId(savedProject.getId())
                                                       .dueDate(LocalDate.now().minusWeeks(1))
                                                       .assignedTo("Desenvolvedor Atrasado")
                                                       .completed(false)
                                                       .priority("BAIXA")
                                                       .estimatedHours(2)
                                                       .build();

        // Act
        Activity savedActivity = createActivityUseCase.execute(activityDTO);

        // Assert
        assertEquals(activityDTO.getDueDate(), savedActivity.getDueDate());
    }
}