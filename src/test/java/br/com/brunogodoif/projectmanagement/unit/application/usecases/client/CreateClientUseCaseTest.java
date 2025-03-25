package br.com.brunogodoif.projectmanagement.unit.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityDuplicateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateClientUseCaseTest {

    @Mock
    private ClientGatewayInterface clientGateway;

    @InjectMocks
    private CreateClientUseCase createClientUseCase;

    private ClientInputDTO validClientInputDTO;
    private Client expectedClient;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        validClientInputDTO = ClientInputDTO.builder()
                                            .id(clientId)
                                            .name("Empresa Teste Brasil")
                                            .email("contato@empresateste.com.br")
                                            .phone("11 98765-4321")
                                            .companyName("Empresa Teste Ltda")
                                            .address("Av. Paulista, 1000, São Paulo, SP")
                                            .active(true)
                                            .build();

        expectedClient = new Client(
                clientId,
                validClientInputDTO.getName(),
                validClientInputDTO.getEmail(),
                validClientInputDTO.getPhone(),
                validClientInputDTO.getCompanyName(),
                validClientInputDTO.getAddress(),
                null,
                null,
                validClientInputDTO.isActive()
        );
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void shouldCreateClientSuccessfully() {
        // Arrange
        when(clientGateway.existsByEmail(validClientInputDTO.getEmail())).thenReturn(false);
        when(clientGateway.save(any(Client.class))).thenReturn(expectedClient);

        // Act
        Client result = createClientUseCase.execute(validClientInputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(clientId, result.getId());
        assertEquals(validClientInputDTO.getName(), result.getName());
        assertEquals(validClientInputDTO.getEmail(), result.getEmail());
        assertEquals(validClientInputDTO.getPhone(), result.getPhone());
        assertEquals(validClientInputDTO.getCompanyName(), result.getCompanyName());
        assertEquals(validClientInputDTO.getAddress(), result.getAddress());
        assertTrue(result.isActive());

        verify(clientGateway, times(1)).existsByEmail(validClientInputDTO.getEmail());
        verify(clientGateway, times(1)).save(any(Client.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email já existe")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(clientGateway.existsByEmail(validClientInputDTO.getEmail())).thenReturn(true);

        // Act & Assert
        EntityDuplicateException exception = assertThrows(EntityDuplicateException.class, () -> {
            createClientUseCase.execute(validClientInputDTO);
        });

        assertEquals("Client with email " + validClientInputDTO.getEmail() + " already exists", exception.getMessage());

        verify(clientGateway, times(1)).existsByEmail(validClientInputDTO.getEmail());
        verify(clientGateway, never()).save(any(Client.class));
    }
}