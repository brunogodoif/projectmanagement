package br.com.brunogodoif.projectmanagement.unit.application.usecases.client;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.client.ListClientsUseCase;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListClientsUseCaseTest {

    @Mock
    private ClientGatewayInterface clientGateway;

    @InjectMocks
    private ListClientsUseCase listClientsUseCase;

    private Client mockClient1;
    private Client mockClient2;

    @BeforeEach
    void setUp() {
        mockClient1 = new Client(
                UUID.randomUUID(),
                "Escola Futuro Brilhante",
                "contato@futurobrilhante.edu.br",
                "11 5555-1111",
                "Escola Futuro Brilhante Ltda",
                "Rua da Educação, 100, São Paulo, SP",
                LocalDateTime.now().minusYears(2),
                LocalDateTime.now().minusMonths(3),
                true
        );

        mockClient2 = new Client(
                UUID.randomUUID(),
                "Hospital Santa Saúde",
                "contato@santasaude.com.br",
                "21 3333-7777",
                "Hospital Santa Saúde S.A.",
                "Av. da Saúde, 500, Rio de Janeiro, RJ",
                LocalDateTime.now().minusYears(1),
                LocalDateTime.now().minusMonths(1),
                true
        );
    }

    @Test
    @DisplayName("Deve listar todos os clientes ativos")
    void shouldListAllActiveClients() {
        // Arrange
        List<Client> expectedClients = Arrays.asList(mockClient1, mockClient2);
        when(clientGateway.findAllActive()).thenReturn(expectedClients);

        // Act
        List<Client> result = listClientsUseCase.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(mockClient1));
        assertTrue(result.contains(mockClient2));

        verify(clientGateway, times(1)).findAllActive();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há clientes ativos")
    void shouldReturnEmptyListWhenNoActiveClients() {
        // Arrange
        when(clientGateway.findAllActive()).thenReturn(Collections.emptyList());

        // Act
        List<Client> result = listClientsUseCase.execute();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(clientGateway, times(1)).findAllActive();
    }
}
