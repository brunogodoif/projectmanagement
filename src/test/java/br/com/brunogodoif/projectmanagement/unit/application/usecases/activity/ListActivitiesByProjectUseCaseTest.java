package br.com.brunogodoif.projectmanagement.unit.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.activity.ListActivitiesByProjectUseCase;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListActivitiesByProjectUseCaseTest {

    @Mock
    private ActivityGatewayInterface activityGateway;

    @Mock
    private ProjectGatewayInterface projectGateway;

    @InjectMocks
    private ListActivitiesByProjectUseCase listActivitiesByProjectUseCase;

    private UUID projectId;
    private Project mockProject;
    private Activity mockActivity1;
    private Activity mockActivity2;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();

        Client client = new Client(
                UUID.randomUUID(),
                "Construtora Horizonte",
                "contato@construtora-horizonte.com.br",
                "11 4444-5555",
                "Construtora Horizonte Ltda",
                "Av. das Construções, 300, São Paulo, SP",
                LocalDateTime.now().minusYears(2),
                LocalDateTime.now().minusMonths(6),
                true
        );

        mockProject = new Project(
                projectId,
                "Residencial Primavera",
                "Construção de condomínio residencial com 5 torres",
                client,
                LocalDate.now().minusMonths(3),
                LocalDate.now().plusYears(2),
                ProjectStatus.IN_PROGRESS,
                "Roberto Engenheiro",
                "Aprovado pela prefeitura com licença completa",
                false,
                LocalDateTime.now().minusMonths(3),
                LocalDateTime.now().minusMonths(1)
        );

        mockActivity1 = new Activity(
                UUID.randomUUID(),
                "Fundação Torre A",
                "Escavação e preparação da fundação para a Torre A",
                mockProject,
                LocalDate.now().plusWeeks(2),
                "Equipe Fundações",
                false,
                "ALTA",
                120,
                LocalDateTime.now().minusWeeks(3),
                LocalDateTime.now().minusWeeks(2)
        );

        mockActivity2 = new Activity(
                UUID.randomUUID(),
                "Preparação de Terreno",
                "Limpeza e nivelamento de terreno completo",
                mockProject,
                LocalDate.now().plusDays(10),
                "Equipe Terraplanagem",
                true,
                "ALTA",
                80,
                LocalDateTime.now().minusWeeks(3),
                LocalDateTime.now().minusWeeks(1)
        );
    }

    @Test
    @DisplayName("Deve listar atividades por projeto com sucesso")
    void shouldListActivitiesByProjectSuccessfully() {
        // Arrange
        List<Activity> expectedActivities = Arrays.asList(mockActivity1, mockActivity2);
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.findByProjectId(projectId)).thenReturn(expectedActivities);

        // Act
        List<Activity> result = listActivitiesByProjectUseCase.execute(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockActivity1));
        assertTrue(result.contains(mockActivity2));

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).findByProjectId(projectId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há atividades para o projeto")
    void shouldReturnEmptyListWhenNoActivitiesFound() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.findByProjectId(projectId)).thenReturn(Collections.emptyList());

        // Act
        List<Activity> result = listActivitiesByProjectUseCase.execute(projectId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).findByProjectId(projectId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o projeto não é encontrado")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            listActivitiesByProjectUseCase.execute(projectId);
        });

        assertEquals("Project not found with ID: " + projectId, exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, never()).findByProjectId(any(UUID.class));
    }
}
