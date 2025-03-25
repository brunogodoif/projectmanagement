package br.com.brunogodoif.projectmanagement.unit.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.project.UpdateProjectUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
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
class UpdateProjectUseCaseTest {

    @Mock
    private ProjectGatewayInterface projectGateway;

    @Mock
    private ClientGatewayInterface clientGateway;

    @InjectMocks
    private UpdateProjectUseCase updateProjectUseCase;

    private UUID projectId;
    private UUID existingClientId;
    private UUID newClientId;
    private Client existingClient;
    private Client newClient;
    private Project existingProject;
    private ProjectInputDTO projectInputDTO;
    private Project updatedProject;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        existingClientId = UUID.randomUUID();
        newClientId = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusMonths(3);
        LocalDateTime updatedAt = LocalDateTime.now().minusWeeks(2);

        existingClient = new Client(
                existingClientId,
                "Laboratório Saúde Brasil",
                "contato@saudebrasil.com.br",
                "11 2222-3333",
                "Laboratório Saúde Brasil Ltda",
                "Av. da Saúde, 400, São Paulo, SP",
                createdAt.minusMonths(6),
                updatedAt.minusMonths(1),
                true
        );

        newClient = new Client(
                newClientId,
                "Hospital Santa Clara",
                "contato@santaclara.com.br",
                "11 3333-4444",
                "Hospital Santa Clara S.A.",
                "Rua dos Médicos, 300, São Paulo, SP",
                createdAt.minusYears(1),
                updatedAt.minusMonths(2),
                true
        );

        existingProject = new Project(
                projectId,
                "Sistema de Gestão Laboratorial",
                "Sistema para gerenciamento de resultados de exames e laudos",
                existingClient,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(3),
                ProjectStatus.IN_PROGRESS,
                "Pedro Coordenador",
                "Projeto prioritário para integração com convênios",
                false,
                createdAt,
                updatedAt
        );

        projectInputDTO = ProjectInputDTO.builder()
                                         .name("Sistema de Gestão Laboratorial - V2")
                                         .description("Sistema atualizado para gerenciamento de exames, laudos e integração com hospitais")
                                         .clientId(newClientId)
                                         .startDate(LocalDate.now().minusMonths(2))
                                         .endDate(LocalDate.now().plusMonths(4))
                                         .status(ProjectStatus.IN_PROGRESS)
                                         .manager("Juliana Coordenadora")
                                         .notes("Escopo ampliado com módulo de telemedicina")
                                         .build();

        updatedProject = new Project(
                projectId,
                projectInputDTO.getName(),
                projectInputDTO.getDescription(),
                newClient,
                projectInputDTO.getStartDate(),
                projectInputDTO.getEndDate(),
                projectInputDTO.getStatus(),
                projectInputDTO.getManager(),
                projectInputDTO.getNotes(),
                false,
                createdAt,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Deve atualizar projeto com cliente diferente")
    void shouldUpdateProjectWithDifferentClient() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(clientGateway.findById(newClientId)).thenReturn(Optional.of(newClient));
        when(projectGateway.save(any(Project.class))).thenReturn(updatedProject);

        // Act
        Project result = updateProjectUseCase.execute(projectId, projectInputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals(projectInputDTO.getName(), result.getName());
        assertEquals(projectInputDTO.getDescription(), result.getDescription());
        assertEquals(newClient, result.getClient());
        assertEquals(projectInputDTO.getStartDate(), result.getStartDate());
        assertEquals(projectInputDTO.getEndDate(), result.getEndDate());
        assertEquals(projectInputDTO.getStatus(), result.getStatus());
        assertEquals(projectInputDTO.getManager(), result.getManager());
        assertEquals(projectInputDTO.getNotes(), result.getNotes());
        assertEquals(existingProject.isDeleted(), result.isDeleted());
        assertEquals(existingProject.getCreatedAt(), result.getCreatedAt());
        assertNotEquals(existingProject.getUpdatedAt(), result.getUpdatedAt());

        verify(projectGateway, times(1)).findById(projectId);
        verify(clientGateway, times(1)).findById(newClientId);
        verify(projectGateway, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve atualizar projeto com mesmo cliente")
    void shouldUpdateProjectWithSameClient() {
        // Arrange
        ProjectInputDTO dtoWithSameClient = ProjectInputDTO.builder()
                                                           .name("Sistema de Gestão Laboratorial - V2")
                                                           .description("Sistema atualizado para gerenciamento de exames e laudos")
                                                           .clientId(existingClientId)
                                                           .startDate(LocalDate.now().minusMonths(2))
                                                           .endDate(LocalDate.now().plusMonths(4))
                                                           .status(ProjectStatus.IN_PROGRESS)
                                                           .manager("Juliana Coordenadora")
                                                           .notes("Adicionado módulo de notificações")
                                                           .build();

        Project updatedProjectSameClient = new Project(
                projectId,
                dtoWithSameClient.getName(),
                dtoWithSameClient.getDescription(),
                existingClient,
                dtoWithSameClient.getStartDate(),
                dtoWithSameClient.getEndDate(),
                dtoWithSameClient.getStatus(),
                dtoWithSameClient.getManager(),
                dtoWithSameClient.getNotes(),
                false,
                existingProject.getCreatedAt(),
                LocalDateTime.now()
        );

        when(projectGateway.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(projectGateway.save(any(Project.class))).thenReturn(updatedProjectSameClient);

        // Act
        Project result = updateProjectUseCase.execute(projectId, dtoWithSameClient);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals(dtoWithSameClient.getName(), result.getName());
        assertEquals(existingClient, result.getClient());

        verify(projectGateway, times(1)).findById(projectId);
        verify(clientGateway, never()).findById(any(UUID.class));
        verify(projectGateway, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando projeto não é encontrado")
    void shouldThrowExceptionWhenProjectNotFound() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            updateProjectUseCase.execute(projectId, projectInputDTO);
        });

        assertEquals("Project not found with ID: " + projectId, exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(clientGateway, never()).findById(any(UUID.class));
        verify(projectGateway, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não é encontrado")
    void shouldThrowExceptionWhenClientNotFound() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(clientGateway.findById(newClientId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            updateProjectUseCase.execute(projectId, projectInputDTO);
        });

        assertEquals("Client not found with ID: " + newClientId, exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(clientGateway, times(1)).findById(newClientId);
        verify(projectGateway, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro na operação")
    void shouldThrowExceptionWhenOperationFails() {
        // Arrange
        when(projectGateway.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(clientGateway.findById(newClientId)).thenReturn(Optional.of(newClient));
        when(projectGateway.save(any(Project.class))).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        BusinessOperationException exception = assertThrows(BusinessOperationException.class, () -> {
            updateProjectUseCase.execute(projectId, projectInputDTO);
        });

        assertEquals("Failed to update project", exception.getMessage());

        verify(projectGateway, times(1)).findById(projectId);
        verify(clientGateway, times(1)).findById(newClientId);
        verify(projectGateway, times(1)).save(any(Project.class));
    }
}