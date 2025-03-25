package br.com.brunogodoif.projectmanagement.unit.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateProjectUseCaseTest {

    @Mock
    private ProjectGatewayInterface projectGateway;

    @Mock
    private ClientGatewayInterface clientGateway;

    @InjectMocks
    private CreateProjectUseCase createProjectUseCase;

    private UUID projectId;
    private UUID clientId;
    private Client mockClient;
    private ProjectInputDTO validProjectInputDTO;
    private Project expectedProject;

    @BeforeEach
    void setUp() {
        projectId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        mockClient = new Client(
                clientId,
                "TechSolutions Brasil",
                "contato@techsolutions.com.br",
                "11 3333-4444",
                "TechSolutions Sistemas Ltda",
                "Av. Engenheiro Luís Carlos Berrini, 1500, São Paulo, SP",
                LocalDateTime.now().minusMonths(6),
                LocalDateTime.now().minusMonths(3),
                true
        );

        validProjectInputDTO = ProjectInputDTO.builder()
                                              .id(projectId)
                                              .name("Portal de Atendimento ao Cliente")
                                              .description("Desenvolvimento de portal web para autoatendimento de clientes")
                                              .clientId(clientId)
                                              .startDate(LocalDate.now())
                                              .endDate(LocalDate.now().plusMonths(4))
                                              .status(ProjectStatus.OPEN)
                                              .manager("Ricardo Gestor")
                                              .notes("Integração com CRM existente")
                                              .build();

        expectedProject = new Project(
                projectId,
                validProjectInputDTO.getName(),
                validProjectInputDTO.getDescription(),
                mockClient,
                validProjectInputDTO.getStartDate(),
                validProjectInputDTO.getEndDate(),
                validProjectInputDTO.getStatus(),
                validProjectInputDTO.getManager(),
                validProjectInputDTO.getNotes(),
                false,
                null,
                null
        );
    }

    @Test
    @DisplayName("Deve criar um projeto com sucesso")
    void shouldCreateProjectSuccessfully() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.of(mockClient));
        when(projectGateway.save(any(Project.class))).thenReturn(expectedProject);

        // Act
        Project result = createProjectUseCase.execute(validProjectInputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        assertEquals(validProjectInputDTO.getName(), result.getName());
        assertEquals(validProjectInputDTO.getDescription(), result.getDescription());
        assertEquals(mockClient, result.getClient());
        assertEquals(validProjectInputDTO.getStartDate(), result.getStartDate());
        assertEquals(validProjectInputDTO.getEndDate(), result.getEndDate());
        assertEquals(validProjectInputDTO.getStatus(), result.getStatus());
        assertEquals(validProjectInputDTO.getManager(), result.getManager());
        assertEquals(validProjectInputDTO.getNotes(), result.getNotes());
        assertFalse(result.isDeleted());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando cliente não é encontrado")
    void shouldThrowExceptionWhenClientNotFound() {
        // Arrange
        when(clientGateway.findById(clientId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            createProjectUseCase.execute(validProjectInputDTO);
        });

        assertEquals("Client not found with ID: " + clientId, exception.getMessage());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Deve criar projeto com cliente encontrado")
    void shouldCreateProjectWithFoundClient() {
        // Arrange
        Client differentClientWithSameId = new Client(
                clientId,
                "Nome Diferente",
                "email.diferente@teste.com.br",
                "11 9999-9999",
                "Empresa Diferente",
                "Endereço Diferente",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );

        when(clientGateway.findById(clientId)).thenReturn(Optional.of(differentClientWithSameId));
        when(projectGateway.save(any(Project.class))).thenAnswer(invocation -> {
            Project savedProject = invocation.getArgument(0);
            // Retornar o mesmo projeto que foi passado para save()
            return savedProject;
        });

        // Act
        Project result = createProjectUseCase.execute(validProjectInputDTO);

        // Assert
        assertNotNull(result);
        assertEquals(differentClientWithSameId, result.getClient());

        verify(clientGateway, times(1)).findById(clientId);
        verify(projectGateway, times(1)).save(any(Project.class));
    }
}
