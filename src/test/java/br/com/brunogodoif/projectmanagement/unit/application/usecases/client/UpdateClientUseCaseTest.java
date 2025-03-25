package br.com.brunogodoif.projectmanagement.unit.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.UpdateClientUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityDuplicateException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateClientUseCaseTest {

    @Mock
    private ClientGatewayInterface clientGateway;

    @InjectMocks
    private UpdateClientUseCase updateClientUseCase;

    private UUID clientId;
    private Client existingClient;
    private ClientInputDTO updateClientInputDTO;
    private Client updatedClient;
    private LocalDateTime createdAt;
    private LocalDateTime originalUpdatedAt;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        createdAt = LocalDateTime.now().minusMonths(3);
        originalUpdatedAt = LocalDateTime.now().minusWeeks(1);

        existingClient = new Client(
                clientId,
                "Supermercado ABC",
                "contato@superabc.com.br",
                "11 5555-1234",
                "Supermercado ABC Ltda",
                "Rua das Mercadorias, 100, São Paulo, SP",
                createdAt,
                originalUpdatedAt,
                true
        );

        updateClientInputDTO = ClientInputDTO.builder()
                                             .name("Supermercado ABC Atualizado")
                                             .email("novo.contato@superabc.com.br")
                                             .phone("11 5555-9876")
                                             .companyName("Supermercado ABC Atacadista Ltda")
                                             .address("Av. das Compras, 200, São Paulo, SP")
                                             .active(true)
                                             .build();

        updatedClient = new Client(
                clientId,
                updateClientInputDTO.getName(),
                updateClientInputDTO.getEmail(),
                updateClientInputDTO.getPhone(),
                updateClientInputDTO.getCompanyName(),
                updateClientInputDTO.getAddress(),
                createdAt,
                LocalDateTime.now(),
                updateClientInputDTO.isActive()
        );
    }

    @Test
    @DisplayName("Deve atualizar um cliente com sucesso")
    void shouldUpdateClientSuccessfully() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientGateway.existsByEmail(updateClientInputDTO.getEmail())).thenReturn(false);
        when(clientGateway.save(any(Client.class))).thenReturn(updatedClient);

        // Act
        Client result = updateClientUseCase.execute(clientId, updateClientInputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals(updateClientInputDTO.getName(), result.getName());
        assertEquals(updateClientInputDTO.getEmail(), result.getEmail());
        assertEquals(updateClientInputDTO.getPhone(), result.getPhone());
        assertEquals(updateClientInputDTO.getCompanyName(), result.getCompanyName());
        assertEquals(updateClientInputDTO.getAddress(), result.getAddress());
        assertEquals(updateClientInputDTO.isActive(), result.isActive());
        assertEquals(createdAt, result.getCreatedAt());
        assertNotEquals(originalUpdatedAt, result.getUpdatedAt());

        verify(clientGateway, times(1)).findById(clientId);
        verify(clientGateway, times(1)).existsByEmail(updateClientInputDTO.getEmail());
        verify(clientGateway, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Deve atualizar cliente quando email não muda")
    void shouldUpdateClientWhenEmailDoesntChange() {
        // Arrange
        ClientInputDTO dtoWithSameEmail = ClientInputDTO.builder()
                                                        .name("Supermercado ABC Atualizado")
                                                        .email(existingClient.getEmail()) // Mesmo email
                                                        .phone("11 5555-9876")
                                                        .companyName("Supermercado ABC Atacadista Ltda")
                                                        .address("Av. das Compras, 200, São Paulo, SP")
                                                        .active(true)
                                                        .build();

        when(clientGateway.findById(clientId)).thenReturn(Optional.of(existingClient));
        // Não deveria verificar se o email existe, pois é o mesmo
        when(clientGateway.save(any(Client.class))).thenReturn(updatedClient);

        // Act
        Client result = updateClientUseCase.execute(clientId, dtoWithSameEmail);

        // Assert
        assertNotNull(result);

        verify(clientGateway, times(1)).findById(clientId);
        // Não deve chamar existsByEmail quando o email não muda
        verify(clientGateway, never()).existsByEmail(dtoWithSameEmail.getEmail());
        verify(clientGateway, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não é encontrado")
    void shouldThrowExceptionWhenClientNotFound() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            updateClientUseCase.execute(clientId, updateClientInputDTO);
        });

        assertEquals("Client not found with ID: " + clientId, exception.getMessage());

        verify(clientGateway, times(1)).findById(clientId);
        verify(clientGateway, never()).existsByEmail(anyString());
        verify(clientGateway, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando novo email já existe")
    void shouldThrowExceptionWhenNewEmailAlreadyExists() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientGateway.existsByEmail(updateClientInputDTO.getEmail())).thenReturn(true);

        // Act & Assert
        EntityDuplicateException exception = assertThrows(EntityDuplicateException.class, () -> {
            updateClientUseCase.execute(clientId, updateClientInputDTO);
        });

        assertEquals("Client with email " + updateClientInputDTO.getEmail() + " already exists", exception.getMessage());

        verify(clientGateway, times(1)).findById(clientId);
        verify(clientGateway, times(1)).existsByEmail(updateClientInputDTO.getEmail());
        verify(clientGateway, never()).save(any(Client.class));
    }
}