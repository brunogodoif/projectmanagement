package br.com.brunogodoif.projectmanagement.integration.infrastructure.gateways;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.ActivityGateway;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.ClientGateway;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.ProjectGateway;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.exceptions.DatabaseOperationException;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ActivityRepository;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ClientRepository;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ActivityGatewayTest extends BaseIntegrationTest {

    @Autowired
    private ActivityGateway activityGateway;

    @Autowired
    private ProjectGateway projectGateway;

    @Autowired
    private ClientGateway clientGateway;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client savedClient;
    private Project savedProject;

    @BeforeEach
    void setup() {
        activityRepository.deleteAll();
        projectRepository.deleteAll();
        clientRepository.deleteAll();

        // Criar um cliente e um projeto para associar às atividades
        savedClient = clientGateway.save(createSampleClient());
        savedProject = projectGateway.save(createSampleProject(savedClient));
    }

    @Test
    @DisplayName("Deve salvar uma atividade com sucesso")
    void shouldSaveActivitySuccessfully() {
        // Arrange
        Activity activity = createSampleActivity(savedProject);

        // Act
        Activity savedActivity = activityGateway.save(activity);

        // Assert
        assertNotNull(savedActivity.getId());
        assertEquals(activity.getTitle(), savedActivity.getTitle());
        assertEquals(activity.getDescription(), savedActivity.getDescription());
        assertEquals(activity.getProject().getId(), savedActivity.getProject().getId());
        assertEquals(activity.getDueDate(), savedActivity.getDueDate());
        assertEquals(activity.getAssignedTo(), savedActivity.getAssignedTo());
        assertEquals(activity.isCompleted(), savedActivity.isCompleted());
        assertEquals(activity.getPriority(), savedActivity.getPriority());
        assertEquals(activity.getEstimatedHours(), savedActivity.getEstimatedHours());
        assertNotNull(savedActivity.getCreatedAt());
        assertNotNull(savedActivity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve falhar ao salvar atividade com projeto inexistente")
    void shouldFailWhenSavingActivityWithNonExistentProject() {
        // Arrange
        Project nonExistentProject = new Project(
                UUID.randomUUID(),
                "Projeto Inexistente",
                "Descrição",
                savedClient,
                LocalDate.now(),
                LocalDate.now().plusMonths(1),
                ProjectStatus.IN_PROGRESS,
                "Gerente",
                "Notas",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        Activity activity = createSampleActivity(nonExistentProject);

        // Act & Assert
        assertThrows(DatabaseOperationException.class, () -> activityGateway.save(activity));
    }

    @Test
    @DisplayName("Deve atualizar uma atividade existente com sucesso")
    void shouldUpdateActivitySuccessfully() {
        // Arrange
        Activity activity = createSampleActivity(savedProject);
        Activity savedActivity = activityGateway.save(activity);

        // Criar nova atividade com valores atualizados
        Activity updatedActivityData = new Activity(
                savedActivity.getId(),
                "Atividade Atualizada",
                savedActivity.getDescription(),
                savedActivity.getProject(),
                savedActivity.getDueDate(),
                savedActivity.getAssignedTo(),
                true, // Atualizar para concluído
                savedActivity.getPriority(),
                savedActivity.getEstimatedHours(),
                savedActivity.getCreatedAt(),
                LocalDateTime.now()
        );

        // Act
        Activity updatedActivity = activityGateway.save(updatedActivityData);

        // Assert
        assertEquals(savedActivity.getId(), updatedActivity.getId());
        assertEquals("Atividade Atualizada", updatedActivity.getTitle());
        assertTrue(updatedActivity.isCompleted());
        assertEquals(savedActivity.getProject().getId(), updatedActivity.getProject().getId());
    }

    @Test
    @DisplayName("Deve encontrar uma atividade por ID com sucesso")
    void shouldFindActivityByIdSuccessfully() {
        // Arrange
        Activity activity = createSampleActivity(savedProject);
        Activity savedActivity = activityGateway.save(activity);

        // Act
        Optional<Activity> foundActivity = activityGateway.findById(savedActivity.getId());

        // Assert
        assertTrue(foundActivity.isPresent());
        assertEquals(savedActivity.getId(), foundActivity.get().getId());
        assertEquals(savedActivity.getTitle(), foundActivity.get().getTitle());
        assertEquals(savedActivity.getProject().getId(), foundActivity.get().getProject().getId());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar atividade com ID inexistente")
    void shouldReturnEmptyOptionalWhenFindingNonExistentActivity() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Activity> foundActivity = activityGateway.findById(nonExistentId);

        // Assert
        assertTrue(foundActivity.isEmpty());
    }

    @Test
    @DisplayName("Deve listar todas as atividades com sucesso")
    void shouldListAllActivitiesSuccessfully() {
        // Arrange
        Activity activity1 = createSampleActivity(savedProject);

        Activity activity2 = new Activity(
                UUID.randomUUID(),
                "Outra Atividade",
                "Descrição da outra atividade",
                savedProject,
                LocalDate.now().plusDays(10),
                "Outro Responsável",
                true, // Segunda atividade já está concluída
                "MÉDIA",
                16,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        activityGateway.save(activity1);
        activityGateway.save(activity2);

        // Act
        List<Activity> activities = activityGateway.findAll();

        // Assert
        assertEquals(2, activities.size());
    }

    @Test
    @DisplayName("Deve listar atividades por projeto com sucesso")
    void shouldListActivitiesByProjectSuccessfully() {
        // Arrange
        Activity activity1 = createSampleActivity(savedProject);

        Activity activity2 = new Activity(
                UUID.randomUUID(),
                "Outra Atividade",
                "Descrição da outra atividade",
                savedProject,
                LocalDate.now().plusDays(10),
                "Outro Responsável",
                true,
                "MÉDIA",
                16,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Criar outro projeto e atividade associada
        Project anotherProject = projectGateway.save(new Project(
                UUID.randomUUID(),
                "Outro Projeto",
                "Descrição de outro projeto",
                savedClient,
                LocalDate.now(),
                LocalDate.now().plusMonths(2),
                ProjectStatus.OPEN,
                "Outro Gerente",
                "Notas sobre outro projeto",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        ));

        Activity activity3 = new Activity(
                UUID.randomUUID(),
                "Atividade de Outro Projeto",
                "Descrição da atividade de outro projeto",
                anotherProject,
                LocalDate.now().plusDays(15),
                "Desenvolvedor de Outro Projeto",
                false,
                "ALTA",
                24,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        activityGateway.save(activity1);
        activityGateway.save(activity2);
        activityGateway.save(activity3);

        // Act
        List<Activity> projectActivities = activityGateway.findByProjectId(savedProject.getId());
        List<Activity> otherProjectActivities = activityGateway.findByProjectId(anotherProject.getId());

        // Assert
        assertEquals(2, projectActivities.size());
        assertEquals(1, otherProjectActivities.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar atividades por projeto inexistente")
    void shouldReturnEmptyListWhenFindingActivitiesByNonExistentProject() {
        // Arrange
        UUID nonExistentProjectId = UUID.randomUUID();

        // Act
        List<Activity> activities = activityGateway.findByProjectId(nonExistentProjectId);

        // Assert
        assertTrue(activities.isEmpty());
    }

    @Test
    @DisplayName("Deve listar atividades pendentes por projeto com sucesso")
    void shouldListPendingActivitiesByProjectSuccessfully() {
        // Arrange
        Activity activity1 = createSampleActivity(savedProject); // Por padrão não está concluída

        Activity activity2 = new Activity(
                UUID.randomUUID(),
                "Outra Atividade",
                "Descrição da outra atividade",
                savedProject,
                LocalDate.now().plusDays(10),
                "Outro Responsável",
                true, // Esta está concluída
                "MÉDIA",
                16,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        activityGateway.save(activity1);
        activityGateway.save(activity2);

        // Act
        List<Activity> pendingActivities = activityGateway.findPendingByProjectId(savedProject.getId());

        // Assert
        assertEquals(1, pendingActivities.size());
        assertFalse(pendingActivities.get(0).isCompleted());
    }

    @Test
    @DisplayName("Deve excluir uma atividade com sucesso")
    void shouldDeleteActivitySuccessfully() {
        // Arrange
        Activity activity = createSampleActivity(savedProject);
        Activity savedActivity = activityGateway.save(activity);

        // Act
        activityGateway.deleteById(savedActivity.getId());

        // Assert
        Optional<Activity> foundActivity = activityGateway.findById(savedActivity.getId());
        assertTrue(foundActivity.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir atividade com ID inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentActivity() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> activityGateway.deleteById(nonExistentId));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir atividade com ID nulo")
    void shouldThrowExceptionWhenDeletingWithNullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> activityGateway.deleteById(null));
    }

    private Client createSampleClient() {
        return new Client(
                UUID.randomUUID(),
                "Empresa Teste LTDA",
                "contato@empresateste.com.br",
                "(11) 4321-8765",
                "Empresa Teste Soluções",
                "Av. Paulista, 1000, São Paulo-SP",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );
    }

    private Project createSampleProject(Client client) {
        return new Project(
                UUID.randomUUID(),
                "Projeto Teste",
                "Descrição do projeto de teste",
                client,
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                ProjectStatus.IN_PROGRESS,
                "Gerente Teste",
                "Notas sobre o projeto de teste",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Activity createSampleActivity(Project project) {
        return new Activity(
                UUID.randomUUID(),
                "Implementar Feature X",
                "Descrição detalhada da implementação da feature X",
                project,
                LocalDate.now().plusWeeks(1),
                "Desenvolvedor Teste",
                false,
                "ALTA",
                8,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}