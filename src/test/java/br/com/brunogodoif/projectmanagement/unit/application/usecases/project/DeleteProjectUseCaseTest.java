package br.com.brunogodoif.projectmanagement.unit.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.project.DeleteProjectUseCase;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityInUseException;
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
class DeleteProjectUseCaseTest {

    @Mock
    private ProjectGatewayInterface projectGateway;

    @Mock
    private ActivityGatewayInterface activityGateway;

    @InjectMocks
    private DeleteProjectUseCase deleteProjectUseCase;

    private UUID projectId;
    private Project mockProject;
    private Activity mockActivity;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();

        Client client = new Client(
                UUID.randomUUID(),
                "Indústria Metais Brasil",
                "contato@metaisbrasil.com.br",
                "11 3333-2222",
                "Indústria Metais Brasil S.A.",
                "Rodovia Industrial, Km 50, São Paulo, SP",
                LocalDateTime.now().minusYears(5),
                LocalDateTime.now().minusMonths(8),
                true
        );

        mockProject = new Project(
                projectId,
                "Modernização de Linha de Produção",
                "Atualização completa da linha de produção para Indústria 4.0",
                client,
                LocalDate.now().minusMonths(6),
                LocalDate.now().plusMonths(6),
                ProjectStatus.IN_PROGRESS,
                "Carlos Engenheiro Industrial",
                "Projeto estratégico para competitividade",
                false,
                LocalDateTime.now().minusMonths(6),
                LocalDateTime.now().minusMonths(1)
        );

        mockActivity = new Activity(
                UUID.randomUUID(),
                "Instalação de Sensores IoT",
                "Instalação e configuração de sensores para monitoramento remoto",
                mockProject,
                LocalDate.now().plusWeeks(2),
                "Equipe Automação",
                false,
                "ALTA",
                120,
                LocalDateTime.now().minusMonths(2),
                LocalDateTime.now().minusWeeks(3)
        );
    }

    @Test
    @DisplayName("Deve excluir projeto com sucesso quando não tem atividades")
    void shouldDeleteProjectSuccessfullyWhenNoActivities() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.findByProjectId(projectId)).thenReturn(Collections.emptyList());
        doNothing().when(projectGateway).deleteById(projectId);

        // Act
        assertDoesNotThrow(() -> deleteProjectUseCase.execute(projectId));

        // Assert
        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).findByProjectId(projectId);
        verify(projectGateway, times(1)).deleteById(projectId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando projeto tem atividades associadas")
    void shouldThrowExceptionWhenProjectHasActivities() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.findByProjectId(projectId)).thenReturn(Arrays.asList(mockActivity));

        // Act & Assert
        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> {
            deleteProjectUseCase.execute(projectId);
        });

        assertEquals("Project with ID " + projectId + " cannot be deleted because it has 1 associated activity(ies)",
                     exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).findByProjectId(projectId);
        verify(projectGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando projeto não é encontrado")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            deleteProjectUseCase.execute(projectId);
        });

        assertEquals("Project not found with ID: " + projectId, exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, never()).findByProjectId(any(UUID.class));
        verify(projectGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro na operação")
    void shouldThrowExceptionWhenOperationFails() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(mockProject));
        when(activityGateway.findByProjectId(projectId)).thenReturn(Collections.emptyList());
        doThrow(new RuntimeException("Database error")).when(projectGateway).deleteById(projectId);

        // Act & Assert
        BusinessOperationException exception = assertThrows(BusinessOperationException.class, () -> {
            deleteProjectUseCase.execute(projectId);
        });

        assertEquals("Failed to delete project", exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(activityGateway, times(1)).findByProjectId(projectId);
        verify(projectGateway, times(1)).deleteById(projectId);
    }
}
