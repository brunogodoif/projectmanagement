package br.com.brunogodoif.projectmanagement.unit.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.GetActivityUseCase;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetActivityUseCaseTest {

    @Mock
    private ActivityGatewayInterface activityGateway;

    @InjectMocks
    private GetActivityUseCase getActivityUseCase;

    private UUID activityId;
    private Activity mockActivity;

    @BeforeEach
    void setUp() {
        activityId = UUID.randomUUID();

        Client client = new Client(
                UUID.randomUUID(),
                "Restaurante Sabor Único",
                "contato@saborunico.com.br",
                "11 95555-8888",
                "Restaurante Sabor Único Ltda",
                "Rua dos Sabores, 100, São Paulo, SP",
                LocalDateTime.now().minusMonths(6),
                LocalDateTime.now().minusMonths(2),
                true
        );

        Project project = new Project(
                UUID.randomUUID(),
                "Sistema de Pedidos Online",
                "Desenvolvimento de plataforma para pedidos online",
                client,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(2),
                ProjectStatus.IN_PROGRESS,
                "Fernanda Gestora",
                "Prioridade para entrega mobile",
                false,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(2)
        );

        mockActivity = new Activity(
                activityId,
                "Implementar Carrinho de Compras",
                "Desenvolver funcionalidade de carrinho de compras com persistência local",
                project,
                LocalDate.now().plusWeeks(1),
                "Rodrigo Desenvolvedor",
                false,
                "ALTA",
                24,
                LocalDateTime.now().minusWeeks(2),
                LocalDateTime.now().minusWeeks(1)
        );
    }

    @Test
    @DisplayName("Deve buscar atividade por ID com sucesso")
    void shouldGetActivityByIdSuccessfully() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.of(mockActivity));

        // Act
        Activity result = getActivityUseCase.execute(activityId);

        // Assert
        assertNotNull(result);
        assertEquals(activityId, result.getId());
        assertEquals(mockActivity.getTitle(), result.getTitle());
        assertEquals(mockActivity.getDescription(), result.getDescription());
        assertEquals(mockActivity.getProject(), result.getProject());
        assertEquals(mockActivity.getDueDate(), result.getDueDate());
        assertEquals(mockActivity.getAssignedTo(), result.getAssignedTo());
        assertEquals(mockActivity.isCompleted(), result.isCompleted());
        assertEquals(mockActivity.getPriority(), result.getPriority());
        assertEquals(mockActivity.getEstimatedHours(), result.getEstimatedHours());
        assertEquals(mockActivity.getCreatedAt(), result.getCreatedAt());
        assertEquals(mockActivity.getUpdatedAt(), result.getUpdatedAt());

        verify(activityGateway, times(1)).findById(activityId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a atividade não é encontrada")
    void shouldThrowExceptionWhenActivityNotFound() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            getActivityUseCase.execute(activityId);
        });

        assertEquals("Activity not found with ID: " + activityId, exception.getMessage());

        verify(activityGateway, times(1)).findById(activityId);
    }
}
