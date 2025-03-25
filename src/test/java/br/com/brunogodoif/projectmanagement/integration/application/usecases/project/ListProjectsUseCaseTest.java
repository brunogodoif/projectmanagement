package br.com.brunogodoif.projectmanagement.integration.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.ListProjectsUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListProjectsUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    @Autowired
    private ListProjectsUseCase listProjectsUseCase;

    private Client activeClient;
    private Project activeProject1;
    private Project activeProject2;

    @BeforeEach
    void setUp() {

        // Criar cliente para os projetos
        ClientInputDTO clientDTO = ClientInputDTO.builder().name("Cliente Projetos").email("cliente.projetos@teste.com")
                                                 .phone("11 98765-4321").companyName("Empresa Projetos")
                                                 .address("Rua Projetos, 123").active(true).build();

        activeClient = createClientUseCase.execute(clientDTO);

        // Criar projetos ativos
        ProjectInputDTO activeProject1DTO = ProjectInputDTO.builder().name("Projeto Ativo 1")
                                                           .description("Descrição do Projeto Ativo 1")
                                                           .clientId(activeClient.getId()).startDate(LocalDate.now())
                                                           .endDate(LocalDate.now().plusMonths(3))
                                                           .status(ProjectStatus.OPEN).manager("Gerente Projeto 1")
                                                           .notes("Notas Projeto 1").build();

        ProjectInputDTO activeProject2DTO = ProjectInputDTO.builder().name("Projeto Ativo 2")
                                                           .description("Descrição do Projeto Ativo 2")
                                                           .clientId(activeClient.getId()).startDate(LocalDate.now())
                                                           .endDate(LocalDate.now().plusMonths(3))
                                                           .status(ProjectStatus.IN_PROGRESS)
                                                           .manager("Gerente Projeto 2").notes("Notas Projeto 2")
                                                           .build();

        activeProject1 = createProjectUseCase.execute(activeProject1DTO);
        activeProject2 = createProjectUseCase.execute(activeProject2DTO);
    }

    @Test
    @DisplayName("Deve listar todos os projetos ativos")
    void shouldListAllActiveProjects() {
        // Act
        List<Project> activeProjects = listProjectsUseCase.execute();

        // Assert
        assertNotNull(activeProjects);
        assertTrue(activeProjects.size() >= 2);

        // Verificar se todos os projetos listados estão ativos (não deletados)
        assertTrue(activeProjects.stream().noneMatch(Project::isDeleted));

        // Verificar se os projetos criados estão na lista
        assertTrue(activeProjects.stream().anyMatch(p -> p.getId().equals(activeProject1.getId())));
        assertTrue(activeProjects.stream().anyMatch(p -> p.getId().equals(activeProject2.getId())));
    }

    @Test
    @DisplayName("Deve listar projetos por status")
    void shouldListProjectsByStatus() {
        // Act
        List<Project> openProjects = listProjectsUseCase.executeByStatus(ProjectStatus.OPEN);
        List<Project> inProgressProjects = listProjectsUseCase.executeByStatus(ProjectStatus.IN_PROGRESS);

        // Assert
        assertNotNull(openProjects);
        assertNotNull(inProgressProjects);

        // Verificar projetos OPEN
        assertEquals(1, openProjects.size());
        assertTrue(openProjects.stream().allMatch(p -> p.getStatus() == ProjectStatus.OPEN));

        // Verificar projetos IN_PROGRESS
        assertEquals(2, inProgressProjects.size());
        assertTrue(inProgressProjects.stream().allMatch(p -> p.getStatus() == ProjectStatus.IN_PROGRESS));
    }

    @Test
    @DisplayName("Deve listar projetos por cliente")
    void shouldListProjectsByClient() {
        // Act
        List<Project> clientProjects = listProjectsUseCase.executeByClient(activeClient.getId());

        // Assert
        assertNotNull(clientProjects);
        assertEquals(2, clientProjects.size());

        // Verificar se todos os projetos pertencem ao cliente
        assertTrue(clientProjects.stream().allMatch(p -> p.getClient().getId().equals(activeClient.getId())));
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar projetos de cliente inexistente")
    void shouldReturnEmptyListWhenSearchingProjectsForNonExistentClient() {
        // Act
        List<Project> clientProjects = listProjectsUseCase.executeByClient(java.util.UUID.randomUUID());

        // Assert
        assertNotNull(clientProjects);
        assertTrue(clientProjects.isEmpty());
    }
}