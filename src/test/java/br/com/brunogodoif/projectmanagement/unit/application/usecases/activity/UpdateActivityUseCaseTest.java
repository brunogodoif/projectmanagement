package br.com.brunogodoif.projectmanagement.unit.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.UpdateActivityUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateActivityUseCaseTest {

    @Mock
    private ActivityGatewayInterface activityGateway;

    @Mock
    private ProjectGatewayInterface projectGateway;

    @InjectMocks
    private UpdateActivityUseCase updateActivityUseCase;

    private UUID activityId;
    private UUID existingProjectId;
    private UUID newProjectId;
    private Project existingProject;
    private Project newProject;
    private Activity existingActivity;
    private ActivityInputDTO activityInputDTO;
    private Activity updatedActivity;

    @BeforeEach
    void setUp() {
        activityId = UUID.randomUUID();
        existingProjectId = UUID.randomUUID();
        newProjectId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(2);
        LocalDateTime updatedAt = LocalDateTime.now().minusWeeks(2);

        Client client = new Client(
                UUID.randomUUID(),
                "Editora Conhecimento",
                "contato@editoraconhecimento.com.br",
                "11 3456-7890",
                "Editora Conhecimento Ltda",
                "Rua dos Livros, 300, São Paulo, SP",
                createdAt.minusYears(1),
                updatedAt.minusMonths(3),
                true
        );

        existingProject = new Project(
                existingProjectId,
                "Portal de Educação Online",
                "Desenvolvimento de plataforma educativa com cursos online",
                client,
                LocalDate.now().minusMonths(3),
                LocalDate.now().plusMonths(4),
                ProjectStatus.IN_PROGRESS,
                "Fernanda Coordenadora",
                "Prioridade alta para lançamento no próximo semestre",
                false,
                createdAt.minusMonths(1),
                updatedAt.minusWeeks(3)
        );

        newProject = new Project(
                newProjectId,
                "Aplicativo Móvel Educacional",
                "Desenvolvimento de app para acesso a conteúdo educativo",
                client,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(6),
                ProjectStatus.OPEN,
                "Ricardo Coordenador",
                "Projeto inovador para expansão de mercado",
                false,
                createdAt.minusWeeks(8),
                updatedAt.minusWeeks(4)
        );

        existingActivity = new Activity(
                activityId,
                "Desenvolvimento de Sistema de Avaliação",
                "Implementar funcionalidade de provas e testes online",
                existingProject,
                LocalDate.now().plusWeeks(2),
                "João Desenvolvedor",
                false,
                "ALTA",
                40,
                createdAt,
                updatedAt
        );

        activityInputDTO = ActivityInputDTO.builder()
                                           .title("Sistema de Avaliação com Feedback")
                                           .description("Implementar módulo de avaliação com feedback automático e relatórios")
                                           .projectId(newProjectId)
                                           .dueDate(LocalDate.now().plusWeeks(3))
                                           .assignedTo("Mariana Desenvolvedora")
                                           .completed(false)
                                           .priority("CRÍTICA")
                                           .estimatedHours(60)
                                           .build();

        updatedActivity = new Activity(
                activityId,
                activityInputDTO.getTitle(),
                activityInputDTO.getDescription(),
                newProject,
                activityInputDTO.getDueDate(),
                activityInputDTO.getAssignedTo(),
                activityInputDTO.isCompleted(),
                activityInputDTO.getPriority(),
                activityInputDTO.getEstimatedHours(),
                createdAt,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve atualizar atividade com projeto diferente")
    void shouldUpdateActivityWithDifferentProject() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.of(existingActivity));
        when(projectGateway.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(activityGateway.save(any(Activity.class))).thenReturn(updatedActivity);

        // Act
        Activity result = updateActivityUseCase.execute(activityId, activityInputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(activityId, result.getId());
        assertEquals(activityInputDTO.getTitle(), result.getTitle());
        assertEquals(activityInputDTO.getDescription(), result.getDescription());
        assertEquals(newProject, result.getProject());
        assertEquals(activityInputDTO.getDueDate(), result.getDueDate());
        assertEquals(activityInputDTO.getAssignedTo(), result.getAssignedTo());
        assertEquals(activityInputDTO.isCompleted(), result.isCompleted());
        assertEquals(activityInputDTO.getPriority(), result.getPriority());
        assertEquals(activityInputDTO.getEstimatedHours(), result.getEstimatedHours());
        assertEquals(existingActivity.getCreatedAt(), result.getCreatedAt());
        assertNotEquals(existingActivity.getUpdatedAt(), result.getUpdatedAt());

        verify(activityGateway, times(1)).findById(activityId);
        verify(projectGateway, times(1)).findById(newProjectId);
        verify(activityGateway, times(1)).save(any(Activity.class));
    }

    @Test
    @DisplayName("Deve atualizar atividade com mesmo projeto")
    void shouldUpdateActivityWithSameProject() {
        // Arrange
        ActivityInputDTO dtoWithSameProject = ActivityInputDTO.builder()
                                                              .title("Sistema de Avaliação com Feedback")
                                                              .description("Implementar módulo de avaliação com feedback automático")
                                                              .projectId(existingProjectId)
                                                              .dueDate(LocalDate.now().plusWeeks(3))
                                                              .assignedTo("Mariana Desenvolvedora")
                                                              .completed(true)
                                                              .priority("ALTA")
                                                              .estimatedHours(50)
                                                              .build();

        Activity updatedActivitySameProject = new Activity(
                activityId,
                dtoWithSameProject.getTitle(),
                dtoWithSameProject.getDescription(),
                existingProject,
                dtoWithSameProject.getDueDate(),
                dtoWithSameProject.getAssignedTo(),
                dtoWithSameProject.isCompleted(),
                dtoWithSameProject.getPriority(),
                dtoWithSameProject.getEstimatedHours(),
                existingActivity.getCreatedAt(),
                LocalDateTime.now()
        );

        when(activityGateway.findById(activityId)).thenReturn(Optional.of(existingActivity));
        when(activityGateway.save(any(Activity.class))).thenReturn(updatedActivitySameProject);

        // Act
        Activity result = updateActivityUseCase.execute(activityId, dtoWithSameProject);

        // Assert
        assertNotNull(result);
        assertEquals(activityId, result.getId());
        assertEquals(dtoWithSameProject.getTitle(), result.getTitle());
        assertEquals(existingProject, result.getProject());
        assertTrue(result.isCompleted()); // Verificando se o status completed foi atualizado

        verify(activityGateway, times(1)).findById(activityId);
        verify(projectGateway, never()).findById(any(UUID.class));
        verify(activityGateway, times(1)).save(any(Activity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando atividade não é encontrada")
    void shouldThrowExceptionWhenActivityNotFound() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            updateActivityUseCase.execute(activityId, activityInputDTO);
        });

        assertEquals("Activity not found with ID: " + activityId, exception.getMessage());

        verify(activityGateway, times(1)).findById(activityId);
        verify(projectGateway, never()).findById(any(UUID.class));
        verify(activityGateway, never()).save(any(Activity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando projeto não é encontrado")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.of(existingActivity));
        when(projectGateway.findById(newProjectId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            updateActivityUseCase.execute(activityId, activityInputDTO);
        });

        assertEquals("Project not found with ID: " + newProjectId, exception.getMessage());

        verify(activityGateway, times(1)).findById(activityId);
        verify(projectGateway, times(1)).findById(newProjectId);
        verify(activityGateway, never()).save(any(Activity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro na operação")
    void shouldThrowExceptionWhenOperationFails() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.of(existingActivity));
        when(projectGateway.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(activityGateway.save(any(Activity.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        BusinessOperationException exception = assertThrows(BusinessOperationException.class, () -> {
            updateActivityUseCase.execute(activityId, activityInputDTO);
        });

        assertEquals("Failed to update activity", exception.getMessage());

        verify(activityGateway, times(1)).findById(activityId);
        verify(projectGateway, times(1)).findById(newProjectId);
        verify(activityGateway, times(1)).save(any(Activity.class));
    }
}