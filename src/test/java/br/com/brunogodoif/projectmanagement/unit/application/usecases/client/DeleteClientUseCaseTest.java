package br.com.brunogodoif.projectmanagement.unit.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.DeleteClientUseCase;
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
class DeleteClientUseCaseTest {

    @Mock
    private ClientGatewayInterface clientGateway;

    @Mock
    private ProjectGatewayInterface projectGateway;

    @InjectMocks
    private DeleteClientUseCase deleteClientUseCase;

    private UUID clientId;
    private Client mockClient;
    private Project mockProject;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        mockClient = new Client(
                clientId,
                "Agência Mídia Digital",
                "contato@agenciamidia.com.br",
                "21 98765-4321",
                "Agência Mídia Digital Ltda",
                "Rua das Criações, 500, Rio de Janeiro, RJ",
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now().minusMonths(2),
                true
        );

        mockProject = new Project(
                UUID.randomUUID(),
                "Campanha Marketing Digital",
                "Campanha completa para lançamento de produto",
                mockClient,
                LocalDate.now().minusMonths(1),
                LocalDate.now().plusMonths(2),
                ProjectStatus.IN_PROGRESS,
                "Marcos Marketing",
                "Cliente VIP prioridade máxima",
                false,
                LocalDateTime.now().minusMonths(1),
                LocalDateTime.now().minusWeeks(2)
        );
    }

    @Test
    @DisplayName("Deve excluir cliente com sucesso quando não tem projetos")
    void shouldDeleteClientSuccessfullyWhenNoProjects() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(mockClient));
        when(projectGateway.findByClientId(clientId)).thenReturn(Collections.emptyList());
        doNothing().when(clientGateway).deleteById(clientId);

        // Act
        assertDoesNotThrow(() -> deleteClientUseCase.execute(clientId));

        // Assert
        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, times(1)).findByClientId(clientId);
        verify(clientGateway, times(1)).deleteById(clientId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente tem projetos associados")
    void shouldThrowExceptionWhenClientHasProjects() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(mockClient));
        when(projectGateway.findByClientId(clientId)).thenReturn(Arrays.asList(mockProject));

        // Act & Assert
        EntityInUseException exception = assertThrows(EntityInUseException.class, () -> {
            deleteClientUseCase.execute(clientId);
        });

        assertEquals("Client with ID " + clientId + " cannot be deleted because it has 1 associated project(s)",
                     exception.getMessage());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, times(1)).findByClientId(clientId);
        verify(clientGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não é encontrado")
    void shouldThrowExceptionWhenClientNotFound() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            deleteClientUseCase.execute(clientId);
        });

        assertEquals("Client not found with ID: " + clientId, exception.getMessage());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, never()).findByClientId(any(UUID.class));
        verify(clientGateway, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando ocorre erro na operação")
    void shouldThrowExceptionWhenOperationFails() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(mockClient));
        when(projectGateway.findByClientId(clientId)).thenReturn(Collections.emptyList());
        doThrow(new RuntimeException("Database error")).when(clientGateway).deleteById(clientId);

        // Act & Assert
        BusinessOperationException exception = assertThrows(BusinessOperationException.class, () -> {
            deleteClientUseCase.execute(clientId);
        });

        assertEquals("Failed to delete client", exception.getMessage());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, times(1)).findByClientId(clientId);
        verify(clientGateway, times(1)).deleteById(clientId);
    }
}
