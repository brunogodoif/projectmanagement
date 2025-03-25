package br.com.brunogodoif.projectmanagement.integration.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.CreateActivityUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.ListActivitiesByProjectUseCase;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ListActivitysUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateActivityUseCase createActivityUseCase;

    @Autowired
    private ListActivitiesByProjectUseCase listActivitiesByProjectUseCase;

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    private Project firstProject;
    private Project secondProject;

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

        // Criar primeiro projeto
        ProjectInputDTO firstProjectDTO = ProjectInputDTO.builder()
                                                         .name("Primeiro Projeto")
                                                         .description("Descrição do Primeiro Projeto")
                                                         .clientId(savedClient.getId())
                                                         .startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN)
                                                         .manager("Gerente do Primeiro Projeto")
                                                         .notes("Notas do Primeiro Projeto")
                                                         .build();

        firstProject = createProjectUseCase.execute(firstProjectDTO);

        // Criar segundo projeto
        ProjectInputDTO secondProjectDTO = ProjectInputDTO.builder()
                                                          .name("Segundo Projeto")
                                                          .description("Descrição do Segundo Projeto")
                                                          .clientId(savedClient.getId())
                                                          .startDate(LocalDate.now())
                                                          .endDate(LocalDate.now().plusMonths(3))
                                                          .status(ProjectStatus.IN_PROGRESS)
                                                          .manager("Gerente do Segundo Projeto")
                                                          .notes("Notas do Segundo Projeto")
                                                          .build();

        secondProject = createProjectUseCase.execute(secondProjectDTO);

        // Criar atividades para o primeiro projeto
        ActivityInputDTO activity1DTO = ActivityInputDTO.builder()
                                                        .title("Atividade 1 do Primeiro Projeto")
                                                        .description("Descrição da Atividade 1")
                                                        .projectId(firstProject.getId())
                                                        .dueDate(LocalDate.now().plusWeeks(2))
                                                        .assignedTo("Desenvolvedor 1")
                                                        .completed(false)
                                                        .priority("ALTA")
                                                        .estimatedHours(8)
                                                        .build();

        ActivityInputDTO activity2DTO = ActivityInputDTO.builder()
                                                        .title("Atividade 2 do Primeiro Projeto")
                                                        .description("Descrição da Atividade 2")
                                                        .projectId(firstProject.getId())
                                                        .dueDate(LocalDate.now().plusWeeks(3))
                                                        .assignedTo("Desenvolvedor 2")
                                                        .completed(true)
                                                        .priority("MÉDIA")
                                                        .estimatedHours(6)
                                                        .build();

        // Criar atividade para o segundo projeto
        ActivityInputDTO activity3DTO = ActivityInputDTO.builder()
                                                        .title("Atividade do Segundo Projeto")
                                                        .description("Descrição da Atividade do Segundo Projeto")
                                                        .projectId(secondProject.getId())
                                                        .dueDate(LocalDate.now().plusWeeks(4))
                                                        .assignedTo("Desenvolvedor 3")
                                                        .completed(false)
                                                        .priority("BAIXA")
                                                        .estimatedHours(4)
                                                        .build();

        createActivityUseCase.execute(activity1DTO);
        createActivityUseCase.execute(activity2DTO);
        createActivityUseCase.execute(activity3DTO);
    }

    @Test
    @DisplayName("Deve listar todas as atividades de um projeto")
    void shouldListAllActivitiesOfAProject() {
        // Act
        List<Activity> firstProjectActivities = listActivitiesByProjectUseCase.execute(firstProject.getId());
        List<Activity> secondProjectActivities = listActivitiesByProjectUseCase.execute(secondProject.getId());

        // Assert
        // Verificar atividades do primeiro projeto
        assertNotNull(firstProjectActivities);
        assertEquals(2, firstProjectActivities.size());
        assertTrue(firstProjectActivities.stream().allMatch(a -> a.getProject().getId().equals(firstProject.getId())));

        // Verificar atividades do segundo projeto
        assertNotNull(secondProjectActivities);
        assertEquals(1, secondProjectActivities.size());
        assertTrue(secondProjectActivities.stream().allMatch(a -> a.getProject().getId().equals(secondProject.getId())));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando projeto não tem atividades")
    void shouldReturnEmptyListWhenProjectHasNoActivities() {
        // Criar projeto sem atividades
        ClientInputDTO clientDTO = ClientInputDTO.builder()
                                                 .name("Cliente Projeto Sem Atividades")
                                                 .email("cliente.sematividades@teste.com")
                                                 .phone("11 7777-7777")
                                                 .companyName("Empresa Sem Atividades")
                                                 .address("Rua Sem Atividades, 100")
                                                 .active(true)
                                                 .build();

        Client clientWithoutActivities = createClientUseCase.execute(clientDTO);

        ProjectInputDTO projectWithoutActivitiesDTO = ProjectInputDTO.builder()
                                                                     .name("Projeto Sem Atividades")
                                                                     .description("Descrição do Projeto Sem Atividades")
                                                                     .clientId(clientWithoutActivities.getId())
                                                                     .startDate(LocalDate.now())
                                                                     .endDate(LocalDate.now().plusMonths(3))
                                                                     .status(ProjectStatus.PLANNED)
                                                                     .manager("Gerente do Projeto Sem Atividades")
                                                                     .notes("Notas do Projeto Sem Atividades")
                                                                     .build();

        Project projectWithoutActivities = createProjectUseCase.execute(projectWithoutActivitiesDTO);

        // Act
        List<Activity> projectActivities = listActivitiesByProjectUseCase.execute(projectWithoutActivities.getId());

        // Assert
        assertNotNull(projectActivities);
        assertTrue(projectActivities.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar atividades de projeto inexistente")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        UUID nonExistentProjectId = UUID.randomUUID();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            listActivitiesByProjectUseCase.execute(nonExistentProjectId);
        });

        assertEquals("Project not found with ID: " + nonExistentProjectId, exception.getMessage());
    }
}