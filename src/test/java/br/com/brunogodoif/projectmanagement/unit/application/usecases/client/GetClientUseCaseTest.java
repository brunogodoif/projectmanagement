package br.com.brunogodoif.projectmanagement.unit.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.GetClientUseCase;
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
class GetClientUseCaseTest {

    @Mock
    private ClientGatewayInterface clientGateway;

    @Mock
    private ProjectGatewayInterface projectGateway;

    @InjectMocks
    private GetClientUseCase getClientUseCase;

    private UUID clientId;
    private Client mockClient;
    private Project mockProject1;
    private Project mockProject2;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        mockClient = new Client(
                clientId,
                "Distribuidora Nacional",
                "contato@distribuidoranacional.com.br",
                "21 3456-7890",
                "Distribuidora Nacional S.A.",
                "Av. Brasil, 500, Rio de Janeiro, RJ",
                LocalDateTime.now().minusDays(60),
                LocalDateTime.now().minusDays(30),
                true
        );

        UUID projectId1 = UUID.randomUUID();
        mockProject1 = new Project(
                projectId1,
                "Implementação Sistema ERP",
                "Implementação de sistema ERP para gerenciamento integrado",
                mockClient,
                LocalDate.now().minusMonths(2),
                LocalDate.now().plusMonths(4),
                ProjectStatus.IN_PROGRESS,
                "Paulo Gerente",
                "Projeto prioritário",
                false,
                LocalDateTime.now().minusDays(60),
                LocalDateTime.now().minusDays(30)
        );

        UUID projectId2 = UUID.randomUUID();
        mockProject2 = new Project(
                projectId2,
                "Desenvolvimento de Site Institucional",
                "Criação de novo site responsivo e moderno",
                mockClient,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(1),
                ProjectStatus.OPEN,
                "Mariana Gestora",
                "Seguir diretrizes de marca",
                false,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(15)
        );
    }

    @Test
    @DisplayName("Deve obter cliente por ID com seus projetos")
    void shouldGetClientByIdWithProjects() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(mockClient));
        when(projectGateway.findByClientId(clientId)).thenReturn(Arrays.asList(mockProject1, mockProject2));

        // Act
        Client result = getClientUseCase.execute(clientId);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals(mockClient.getName(), result.getName());
        assertEquals(mockClient.getEmail(), result.getEmail());
        assertEquals(2, result.getProjects().size());
        assertTrue(result.getProjects().contains(mockProject1));
        assertTrue(result.getProjects().contains(mockProject2));

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, times(1)).findByClientId(clientId);
    }

    @Test
    @DisplayName("Deve obter cliente por ID sem projetos")
    void shouldGetClientByIdWithoutProjects() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(mockClient));
        when(projectGateway.findByClientId(clientId)).thenReturn(Collections.emptyList());

        // Act
        Client result = getClientUseCase.execute(clientId);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals(mockClient.getName(), result.getName());
        assertEquals(mockClient.getEmail(), result.getEmail());
        assertTrue(result.getProjects().isEmpty());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, times(1)).findByClientId(clientId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não é encontrado")
    void shouldThrowExceptionWhenClientNotFound() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            getClientUseCase.execute(clientId);
        });

        assertEquals("Client not found with ID: " + clientId, exception.getMessage());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, never()).findByClientId(any(UUID.class));
    }
}