package br.com.brunogodoif.projectmanagement.unit.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.project.ListProjectsUseCase;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListProjectsUseCaseTest {

    @Mock
    private ProjectGatewayInterface projectGateway;

    @InjectMocks
    private ListProjectsUseCase listProjectsUseCase;

    private Project mockProject1;
    private Project mockProject2;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        Client client = new Client(
                clientId,
                "Consultoria Tecnológica Inovação",
                "contato@inovacaotec.com.br",
                "11 3322-4455",
                "Consultoria Tecnológica Inovação Ltda",
                "Av. Paulista, 2000, São Paulo, SP",
                LocalDateTime.now().minusYears(2),
                LocalDateTime.now().minusMonths(1),
                true
        );

        mockProject1 = new Project(
                UUID.randomUUID(),
                "Implementação CRM",
                "Implementação de sistema CRM para gestão de relacionamento com clientes",
                client,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(4),
                ProjectStatus.IN_PROGRESS,
                "Talita Gerente",
                "Prioridade média",
                false,
                LocalDateTime.now().minusMonths(2),
                LocalDateTime.now().minusWeeks(3)
        );

        mockProject2 = new Project(
                UUID.randomUUID(),
                "Migração para Cloud",
                "Migração de infraestrutura para ambiente AWS",
                client,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(2),
                ProjectStatus.OPEN,
                "Henrique DevOps",
                "Alta prioridade - redução de custos",
                false,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(1)
        );
    }

    @Test
    @DisplayName("Deve listar todos os projetos ativos")
    void shouldListAllActiveProjects() {
        // Arrange
        List<Project> expectedProjects = Arrays.asList(mockProject1, mockProject2);
        when(projectGateway.findAllActive()).thenReturn(expectedProjects);

        // Act
        List<Project> result = listProjectsUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockProject1));
        assertTrue(result.contains(mockProject2));

        verify(projectGateway, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há projetos ativos")
    void shouldReturnEmptyListWhenNoActiveProjects() {
        // Arrange
        when(projectGateway.findAllActive()).thenReturn(Collections.emptyList());

        // Act
        List<Project> result = listProjectsUseCase.execute();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectGateway, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Deve listar projetos por status")
    void shouldListProjectsByStatus() {
        // Arrange
        ProjectStatus status = ProjectStatus.IN_PROGRESS;
        List<Project> expectedProjects = Collections.singletonList(mockProject1);
        when(projectGateway.findByStatus(status)).thenReturn(expectedProjects);

        // Act
        List<Project> result = listProjectsUseCase.executeByStatus(status);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(mockProject1));
        assertFalse(result.contains(mockProject2));

        verify(projectGateway, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Deve listar projetos por cliente")
    void shouldListProjectsByClient() {
        // Arrange
        List<Project> expectedProjects = Arrays.asList(mockProject1, mockProject2);
        when(projectGateway.findByClientId(clientId)).thenReturn(expectedProjects);

        // Act
        List<Project> result = listProjectsUseCase.executeByClient(clientId);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockProject1));
        assertTrue(result.contains(mockProject2));

        verify(projectGateway, times(1)).findByClientId(clientId);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há projetos para o status")
    void shouldReturnEmptyListWhenNoProjectsForStatus() {
        // Arrange
        ProjectStatus status = ProjectStatus.COMPLETED;
        when(projectGateway.findByStatus(status)).thenReturn(Collections.emptyList());

        // Act
        List<Project> result = listProjectsUseCase.executeByStatus(status);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectGateway, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há projetos para o cliente")
    void shouldReturnEmptyListWhenNoProjectsForClient() {
        // Arrange
        UUID otherClientId = UUID.randomUUID();
        when(projectGateway.findByClientId(otherClientId)).thenReturn(Collections.emptyList());

        // Act
        List<Project> result = listProjectsUseCase.executeByClient(otherClientId);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(projectGateway, times(1)).findByClientId(otherClientId);
    }
}
