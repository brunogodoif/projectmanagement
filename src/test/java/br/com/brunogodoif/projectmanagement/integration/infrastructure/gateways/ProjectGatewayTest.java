package br.com.brunogodoif.projectmanagement.integration.infrastructure.gateways;

import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.ClientGateway;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.ProjectGateway;
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

public class ProjectGatewayTest extends BaseIntegrationTest {

    @Autowired
    private ProjectGateway projectGateway;

    @Autowired
    private ClientGateway clientGateway;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    private Client savedClient;

    @BeforeEach
    void setup() {
        projectRepository.deleteAll();
        clientRepository.deleteAll();

        // Criar um cliente para associar aos projetos
        savedClient = clientGateway.save(createSampleClient());
    }

    @Test
    @DisplayName("Deve salvar um projeto com sucesso")
    void shouldSaveProjectSuccessfully() {
        // Arrange
        Project project = createSampleProject(savedClient);

        // Act
        Project savedProject = projectGateway.save(project);

        // Assert
        assertNotNull(savedProject.getId());
        assertEquals(project.getName(), savedProject.getName());
        assertEquals(project.getDescription(), savedProject.getDescription());
        assertEquals(project.getClient().getId(), savedProject.getClient().getId());
        assertEquals(project.getStartDate(), savedProject.getStartDate());
        assertEquals(project.getEndDate(), savedProject.getEndDate());
        assertEquals(project.getStatus(), savedProject.getStatus());
        assertEquals(project.getManager(), savedProject.getManager());
        assertEquals(project.getNotes(), savedProject.getNotes());
        assertNotNull(savedProject.getCreatedAt());
        assertNotNull(savedProject.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve atualizar um projeto existente com sucesso")
    void shouldUpdateProjectSuccessfully() {
        // Arrange
        Project project = createSampleProject(savedClient);
        Project savedProject = projectGateway.save(project);

        // Criar um novo projeto com os mesmos dados mas com nome e status atualizados
        Project updatedProjectData = new Project(
                savedProject.getId(),
                "Projeto Atualizado",
                savedProject.getDescription(),
                savedProject.getClient(),
                savedProject.getStartDate(),
                savedProject.getEndDate(),
                ProjectStatus.ON_HOLD, // Alterando o status
                savedProject.getManager(),
                savedProject.getNotes(),
                savedProject.isDeleted(),
                savedProject.getCreatedAt(),
                LocalDateTime.now()
        );

        // Act
        Project updatedProject = projectGateway.save(updatedProjectData);

        // Assert
        assertEquals(savedProject.getId(), updatedProject.getId());
        assertEquals("Projeto Atualizado", updatedProject.getName());
        assertEquals(ProjectStatus.ON_HOLD, updatedProject.getStatus());
        assertEquals(savedProject.getClient().getId(), updatedProject.getClient().getId());
    }

    @Test
    @DisplayName("Deve encontrar um projeto por ID com sucesso")
    void shouldFindProjectByIdSuccessfully() {
        // Arrange
        Project project = createSampleProject(savedClient);
        Project savedProject = projectGateway.save(project);

        // Act
        Optional<Project> foundProject = projectGateway.findById(savedProject.getId());

        // Assert
        assertTrue(foundProject.isPresent());
        assertEquals(savedProject.getId(), foundProject.get().getId());
        assertEquals(savedProject.getName(), foundProject.get().getName());
        assertEquals(savedProject.getClient().getId(), foundProject.get().getClient().getId());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar projeto com ID inexistente")
    void shouldReturnEmptyOptionalWhenFindingNonExistentProject() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act
        Optional<Project> foundProject = projectGateway.findById(nonExistentId);

        // Assert
        assertTrue(foundProject.isEmpty());
    }

    @Test
    @DisplayName("Deve listar todos os projetos com sucesso")
    void shouldListAllProjectsSuccessfully() {
        // Arrange
        Project project1 = createSampleProject(savedClient);

        Project project2 = new Project(
                UUID.randomUUID(),
                "Outro Projeto",
                "Descrição de outro projeto",
                savedClient,
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                ProjectStatus.COMPLETED,
                "Outro Gerente",
                "Notas de outro projeto",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        projectGateway.save(project1);
        projectGateway.save(project2);

        // Act
        List<Project> projects = projectGateway.findAll();

        // Assert
        assertEquals(2, projects.size());
    }

    @Test
    @DisplayName("Deve listar todos os projetos ativos com sucesso")
    void shouldListAllActiveProjectsSuccessfully() {
        // Arrange
        Project project1 = createSampleProject(savedClient);

        Project project2 = new Project(
                UUID.randomUUID(),
                "Outro Projeto",
                "Descrição de outro projeto",
                savedClient,
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                ProjectStatus.COMPLETED,
                "Outro Gerente",
                "Notas de outro projeto",
                false, // Não está excluído
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        projectGateway.save(project1);
        projectGateway.save(project2);

        // Act
        List<Project> activeProjects = projectGateway.findAllActive();

        // Assert
        assertEquals(2, activeProjects.size());
    }

    @Test
    @DisplayName("Deve listar projetos por status com sucesso")
    void shouldListProjectsByStatusSuccessfully() {
        // Arrange
        Project project1 = createSampleProject(savedClient); // IN_PROGRESS por padrão

        Project project2 = new Project(
                UUID.randomUUID(),
                "Outro Projeto",
                "Descrição de outro projeto",
                savedClient,
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                ProjectStatus.COMPLETED,
                "Outro Gerente",
                "Notas de outro projeto",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        projectGateway.save(project1);
        projectGateway.save(project2);

        // Act
        List<Project> inProgressProjects = projectGateway.findByStatus(ProjectStatus.IN_PROGRESS);
        List<Project> completedProjects = projectGateway.findByStatus(ProjectStatus.COMPLETED);

        // Assert
        assertEquals(1, inProgressProjects.size());
        assertEquals(1, completedProjects.size());
        assertEquals(ProjectStatus.IN_PROGRESS, inProgressProjects.get(0).getStatus());
        assertEquals(ProjectStatus.COMPLETED, completedProjects.get(0).getStatus());
    }

    @Test
    @DisplayName("Deve listar projetos por cliente com sucesso")
    void shouldListProjectsByClientSuccessfully() {
        // Arrange
        Project project1 = createSampleProject(savedClient);
        Project project2 = createSampleProject(savedClient);

        // Criar outro cliente e projeto associado
        Client anotherClient = clientGateway.save(new Client(
                UUID.randomUUID(),
                "Outro Cliente LTDA",
                "outro@cliente.com.br",
                "(21) 1234-5678",
                "Outro Cliente Serviços",
                "Rua Qualquer, 123, Rio de Janeiro",
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        ));

        Project project3 = new Project(
                UUID.randomUUID(),
                "Projeto de Outro Cliente",
                "Descrição do projeto de outro cliente",
                anotherClient,
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                ProjectStatus.OPEN,
                "Gerente de Outro Cliente",
                "Notas sobre projeto de outro cliente",
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        projectGateway.save(project1);
        projectGateway.save(project2);
        projectGateway.save(project3);

        // Act
        List<Project> clientProjects = projectGateway.findByClientId(savedClient.getId());
        List<Project> otherClientProjects = projectGateway.findByClientId(anotherClient.getId());

        // Assert
        assertEquals(2, clientProjects.size());
        assertEquals(1, otherClientProjects.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar projetos por cliente inexistente")
    void shouldReturnEmptyListWhenFindingProjectsByNonExistentClient() {
        // Arrange
        UUID nonExistentClientId = UUID.randomUUID();

        // Act
        List<Project> projects = projectGateway.findByClientId(nonExistentClientId);

        // Assert
        assertTrue(projects.isEmpty());
    }

    @Test
    @DisplayName("Deve excluir um projeto com sucesso")
    void shouldDeleteProjectSuccessfully() {
        // Arrange
        Project project = createSampleProject(savedClient);
        Project savedProject = projectGateway.save(project);

        // Act
        projectGateway.deleteById(savedProject.getId());

        // Assert
        Optional<Project> foundProject = projectGateway.findById(savedProject.getId());
        assertTrue(foundProject.isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir projeto com ID inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentProject() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> projectGateway.deleteById(nonExistentId));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir projeto com ID nulo")
    void shouldThrowExceptionWhenDeletingWithNullId() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> projectGateway.deleteById(null));
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
}