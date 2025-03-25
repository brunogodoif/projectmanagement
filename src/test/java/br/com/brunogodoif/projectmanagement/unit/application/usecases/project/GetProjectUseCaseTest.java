package br.com.brunogodoif.projectmanagement.unit.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.project.GetProjectUseCase;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProjectUseCaseTest {

    @Mock
    private ProjectGatewayInterface projectGateway;

    @Mock
    private ActivityGatewayInterface activityGateway;

    @InjectMocks
    private GetProjectUseCase getProjectUseCase;

    private UUID projectId;
    private Project mockProject;
    private Activity mockActivity1;
    private Activity mockActivity2;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();

        Client client = new Client(
                UUID.randomUUID(),
                "Supermercado Economia Total",
                "gerencia@economiatotal.com.br",
                "31 2222-3333",
                "Supermercado Economia Total Ltda",
                "Av. dos Alimentos, 200, Belo Horizonte, MG",
                LocalDateTime.now().minusYears(3),
                LocalDateTime.now().minusMonths(5),
                true
        );

        mockProject = new Project(
                projectId,
                "Sistema de Controle de Estoque",
                "Sistema completo para gerenciamento de estoque e previsão de demanda",
                client,
                LocalDate.now().minusMonths(4),
                LocalDate.now().plusMonths(2),
                ProjectStatus.IN_PROGRESS,
                "Roberta Gestora",
                "Alta prioridade - Implementação necessária antes do período de festas",
                false,
                LocalDateTime.now().minusMonths(4),
                LocalDateTime.now().minusWeeks(1)
        );

        mockActivity1 = new Activity(
                UUID.randomUUID(),
                "Módulo de Inventário",
                "Desenvolvimento de funcionalidade de contagem de inventário",
                mockProject,
                LocalDate.now().plusWeeks(1),
                "Lucas Desenvolvedor",
                false,
                "ALTA",
                40,
                LocalDateTime.now().minusMonths(3),
                LocalDateTime.now().minusMonths(2)
        );

        mockActivity2 = new Activity(
                UUID.randomUUID(),
                "Dashboard de Relatórios",
                "Implementação de dashboard com relatórios de estoque",
                mockProject,
                LocalDate.now().plusDays(10),
                "Amanda Desenvolvedora",
                true,
                "MÉDIA",
                30,
                LocalDateTime.now().minusMonths(2),
                LocalDateTime.now().minusWeeks(3)
        );
    }

    @Test
    @DisplayName("Deve buscar projeto por ID com atividades")
    void shouldGetProjectByIdWithActivities() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.findByProjectId(projectId)).thenReturn(Arrays.asList(mockActivity1, mockActivity2));

        // Act
        Project result = getProjectUseCase.execute(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals(mockProject.getName(), result.getName());
        assertEquals(mockProject.getDescription(), result.getDescription());
        assertEquals(mockProject.getClient(), result.getClient());
        assertEquals(mockProject.getStartDate(), result.getStartDate());
        assertEquals(mockProject.getEndDate(), result.getEndDate());
        assertEquals(mockProject.getStatus(), result.getStatus());
        assertEquals(mockProject.getManager(), result.getManager());
        assertEquals(mockProject.getNotes(), result.getNotes());
        assertEquals(mockProject.isDeleted(), result.isDeleted());
        assertEquals(mockProject.getCreatedAt(), result.getCreatedAt());
        assertEquals(mockProject.getUpdatedAt(), result.getUpdatedAt());

        // Verificar atividades
        assertEquals(2, result.getActivities().size());
        assertTrue(result.getActivities().contains(mockActivity1));
        assertTrue(result.getActivities().contains(mockActivity2));

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).findByProjectId(projectId);
    }

    @Test
    @DisplayName("Deve buscar projeto por ID sem atividades")
    void shouldGetProjectByIdWithoutActivities() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.findByProjectId(projectId)).thenReturn(Collections.emptyList());

        // Act
        Project result = getProjectUseCase.execute(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals(mockProject.getName(), result.getName());

        // Verificar que não há atividades
        assertTrue(result.getActivities().isEmpty());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).findByProjectId(projectId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando projeto não é encontrado")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            getProjectUseCase.execute(projectId);
        });

        assertEquals("Project not found with ID: " + projectId, exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, never()).findByProjectId(any(UUID.class));
    }
}
