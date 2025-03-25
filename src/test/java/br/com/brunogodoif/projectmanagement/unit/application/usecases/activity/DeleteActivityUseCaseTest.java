package br.com.brunogodoif.projectmanagement.unit.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.DeleteActivityUseCase;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteActivityUseCaseTest {

    @Mock
    private ActivityGatewayInterface activityGateway;

    @InjectMocks
    private DeleteActivityUseCase deleteActivityUseCase;

    private UUID activityId;
    private Activity mockActivity;

    @BeforeEach
    void setUp() {
        activityId = UUID.randomUUID();

        Client client = new Client(
                UUID.randomUUID(),
                "Instituto Educacional Aprender",
                "contato@aprender.edu.br",
                "31 3222-4444",
                "Instituto Educacional Aprender Ltda",
                "Av. do Conhecimento, 200, Belo Horizonte, MG",
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now().minusMonths(3),
                true
        );

        Project project = new Project(
                UUID.randomUUID(),
                "Portal de Ensino Online",
                "Desenvolvimento de plataforma EAD completa",
                client,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(4),
                ProjectStatus.IN_PROGRESS,
                "Paulo Coordenador",
                "Entrega com prioridade para módulo de avaliações",
                false,
                LocalDateTime.now().minusMonths(2),
                LocalDateTime.now().minusWeeks(3)
        );

        mockActivity = new Activity(
                activityId,
                "Implementar Sistema de Videoaulas",
                "Desenvolver módulo de upload e streaming de videoaulas",
                project,
                LocalDate.now().plusWeeks(2),
                "Camila Desenvolvedora",
                false,
                "ALTA",
                40,
                LocalDateTime.now().minusWeeks(3),
                LocalDateTime.now().minusWeeks(1)
        );
    }

    @Test
    @DisplayName("Deve excluir atividade com sucesso")
    void shouldDeleteActivitySuccessfully() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.of(mockActivity));
        doNothing().when(activityGateway).deleteById(activityId);

        // Act
        assertDoesNotThrow(() -> deleteActivityUseCase.execute(activityId));

        // Assert
        verify(activityGateway, times(1)).findById(activityId);
        verify(activityGateway, times(1)).deleteById(activityId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando a atividade não é encontrada")
    void shouldThrowExceptionWhenActivityNotFound() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            deleteActivityUseCase.execute(activityId);
        });

        assertEquals("Activity not found with ID: " + activityId, exception.getMessage());

        verify(activityGateway, times(1)).findById(activityId);
        verify(activityGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro na operação")
    void shouldThrowExceptionWhenOperationFails() {
        // Arrange
        when(activityGateway.findById(activityId)).thenReturn(Optional.of(mockActivity));
        doThrow(new RuntimeException("Database error")).when(activityGateway).deleteById(activityId);

        // Act & Assert
        BusinessOperationException exception = assertThrows(BusinessOperationException.class, () -> {
            deleteActivityUseCase.execute(activityId);
        });

        assertEquals("Failed to delete activity", exception.getMessage());

        verify(activityGateway, times(1)).findById(activityId);
        verify(activityGateway, times(1)).deleteById(activityId);
    }
}
