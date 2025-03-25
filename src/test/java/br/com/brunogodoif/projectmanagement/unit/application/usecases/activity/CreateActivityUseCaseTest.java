package br.com.brunogodoif.projectmanagement.unit.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.CreateActivityUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
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
class CreateActivityUseCaseTest {

    @Mock
    private ActivityGatewayInterface activityGateway;

    @Mock
    private ProjectGatewayInterface projectGateway;

    @InjectMocks
    private CreateActivityUseCase createActivityUseCase;

    private UUID activityId;
    private UUID projectId;
    private Project mockProject;
    private ActivityInputDTO validActivityInputDTO;
    private Activity expectedActivity;

    @BeforeEach
    void setUp() {
        activityId = UUID.randomUUID();
        projectId = UUID.randomUUID();

        Client mockClient = new Client(
                UUID.randomUUID(),
                "Cliente Exemplo",
                "cliente@exemplo.com.br",
                "21 99999-8888",
                "Cliente Exemplo Ltda",
                "Rua Exemplo, 123, Rio de Janeiro, RJ",
                LocalDateTime.now().minusMonths(12),
                LocalDateTime.now().minusMonths(6),
                true
        );

        mockProject = new Project(
                projectId,
                "Sistema de Gestão Empresarial",
                "Desenvolvimento de sistema de gestão integrado",
                mockClient,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(10),
                ProjectStatus.IN_PROGRESS,
                "José Coordenador",
                "Sistema crítico para operação",
                false,
                LocalDateTime.now().minusMonths(2),
                LocalDateTime.now().minusWeeks(2)
        );

        validActivityInputDTO = ActivityInputDTO.builder()
                                                .id(activityId)
                                                .title("Desenvolvimento de Dashboard")
                                                .description("Implementação de dashboard com gráficos de desempenho")
                                                .projectId(projectId)
                                                .dueDate(LocalDate.now().plusWeeks(3))
                                                .assignedTo("Ana Desenvolvedora")
                                                .completed(false)
                                                .priority("ALTA")
                                                .estimatedHours(40)
                                                .build();

        expectedActivity = new Activity(
                activityId,
                validActivityInputDTO.getTitle(),
                validActivityInputDTO.getDescription(),
                mockProject,
                validActivityInputDTO.getDueDate(),
                validActivityInputDTO.getAssignedTo(),
                validActivityInputDTO.isCompleted(),
                validActivityInputDTO.getPriority(),
                validActivityInputDTO.getEstimatedHours(),
                null,
                null
        );
    }

    @Test
    @DisplayName("Deve criar uma atividade com sucesso")
    void shouldCreateActivitySuccessfully() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.save(any(Activity.class))).thenReturn(expectedActivity);

        // Act
        Activity result = createActivityUseCase.execute(validActivityInputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(activityId, result.getId());
        assertEquals(validActivityInputDTO.getTitle(), result.getTitle());
        assertEquals(validActivityInputDTO.getDescription(), result.getDescription());
        assertEquals(mockProject, result.getProject());
        assertEquals(validActivityInputDTO.getDueDate(), result.getDueDate());
        assertEquals(validActivityInputDTO.getAssignedTo(), result.getAssignedTo());
        assertEquals(validActivityInputDTO.isCompleted(), result.isCompleted());
        assertEquals(validActivityInputDTO.getPriority(), result.getPriority());
        assertEquals(validActivityInputDTO.getEstimatedHours(), result.getEstimatedHours());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).save(any(Activity.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando projeto não é encontrado")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            createActivityUseCase.execute(validActivityInputDTO);
        });

        assertEquals("Project not found with ID: " + projectId, exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, never()).save(any(Activity.class));
    }
}