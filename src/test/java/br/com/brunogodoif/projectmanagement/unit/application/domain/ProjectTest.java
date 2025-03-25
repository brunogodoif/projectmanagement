package br.com.brunogodoif.projectmanagement.unit.application.domain;

import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    @DisplayName("Deve criar um projeto com construtor padrão")
    void shouldCreateProjectWithDefaultConstructor() {
        Project project = new Project();

        assertNotNull(project.getId());
        assertNotNull(project.getCreatedAt());
        assertNotNull(project.getUpdatedAt());
        assertFalse(project.isDeleted());
        assertTrue(project.getActivities().isEmpty());
    }

    @Test
    @DisplayName("Deve criar um projeto com dados básicos")
    void shouldCreateProjectWithBasicData() {
        String name = "Sistema de Gestão Financeira";
        String description = "Desenvolvimento de sistema de gestão financeira empresarial";
        Client client = createValidClient();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusMonths(6);
        ProjectStatus status = ProjectStatus.OPEN;
        String manager = "João Silva";
        String notes = "Projeto prioritário para o segundo semestre";

        Project project = new Project(name, description, client, startDate, endDate, status, manager, notes);

        assertNotNull(project.getId());
        assertEquals(name, project.getName());
        assertEquals(description, project.getDescription());
        assertEquals(client, project.getClient());
        assertEquals(startDate, project.getStartDate());
        assertEquals(endDate, project.getEndDate());
        assertEquals(status, project.getStatus());
        assertEquals(manager, project.getManager());
        assertEquals(notes, project.getNotes());
        assertNotNull(project.getCreatedAt());
        assertNotNull(project.getUpdatedAt());
        assertFalse(project.isDeleted());
        assertTrue(project.getActivities().isEmpty());
    }

    @Test
    @DisplayName("Deve criar um projeto com todos os parâmetros")
    void shouldCreateProjectWithAllParameters() {
        UUID id = UUID.randomUUID();
        String name = "Portal Intranet";
        String description = "Desenvolvimento do portal intranet corporativo";
        Client client = createValidClient();
        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now().plusMonths(5);
        ProjectStatus status = ProjectStatus.IN_PROGRESS;
        String manager = "Maria Souza";
        String notes = "Integração com sistema de RH necessária";
        boolean isDeleted = false;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(30);
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(15);

        Project project = new Project(id, name, description, client, startDate, endDate, status, manager, notes, isDeleted, createdAt, updatedAt);

        assertEquals(id, project.getId());
        assertEquals(name, project.getName());
        assertEquals(description, project.getDescription());
        assertEquals(client, project.getClient());
        assertEquals(startDate, project.getStartDate());
        assertEquals(endDate, project.getEndDate());
        assertEquals(status, project.getStatus());
        assertEquals(manager, project.getManager());
        assertEquals(notes, project.getNotes());
        assertEquals(isDeleted, project.isDeleted());
        assertEquals(createdAt, project.getCreatedAt());
        assertEquals(updatedAt, project.getUpdatedAt());
        assertTrue(project.getActivities().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome for nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Project(
                    null,
                    "Descrição do projeto",
                    createValidClient(),
                    LocalDate.now(),
                    LocalDate.now().plusMonths(3),
                    ProjectStatus.OPEN,
                    "Gerente Teste",
                    "Notas do projeto"
            );
        });

        assertEquals("Project name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome for vazio")
    void shouldThrowExceptionWhenNameIsEmpty() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Project(
                    "",
                    "Descrição do projeto",
                    createValidClient(),
                    LocalDate.now(),
                    LocalDate.now().plusMonths(3),
                    ProjectStatus.OPEN,
                    "Gerente Teste",
                    "Notas do projeto"
            );
        });

        assertEquals("Project name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente for nulo")
    void shouldThrowExceptionWhenClientIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Project(
                    "Nome do Projeto",
                    "Descrição do projeto",
                    null,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(3),
                    ProjectStatus.OPEN,
                    "Gerente Teste",
                    "Notas do projeto"
            );
        });

        assertEquals("Client is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o status for nulo")
    void shouldThrowExceptionWhenStatusIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Project(
                    "Nome do Projeto",
                    "Descrição do projeto",
                    createValidClient(),
                    LocalDate.now(),
                    LocalDate.now().plusMonths(3),
                    null,
                    "Gerente Teste",
                    "Notas do projeto"
            );
        });

        assertEquals("Project status is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a data final for anterior à data inicial")
    void shouldThrowExceptionWhenEndDateIsBeforeStartDate() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Project(
                    "Nome do Projeto",
                    "Descrição do projeto",
                    createValidClient(),
                    LocalDate.now(),
                    LocalDate.now().minusDays(1),
                    ProjectStatus.OPEN,
                    "Gerente Teste",
                    "Notas do projeto"
            );
        });

        assertEquals("End date cannot be before start date", exception.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar atividade ao projeto")
    void shouldAddActivityToProject() {
        Project project = new Project(
                "Nome do Projeto",
                "Descrição do projeto",
                createValidClient(),
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                ProjectStatus.OPEN,
                "Gerente Teste",
                "Notas do projeto"
        );

        Activity activity = createValidActivity(project);

        project.addActivity(activity);

        assertEquals(1, project.getActivities().size());
        assertTrue(project.getActivities().contains(activity));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar atividade nula")
    void shouldThrowExceptionWhenAddingNullActivity() {
        Project project = new Project(
                "Nome do Projeto",
                "Descrição do projeto",
                createValidClient(),
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                ProjectStatus.OPEN,
                "Gerente Teste",
                "Notas do projeto"
        );

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            project.addActivity(null);
        });

        assertEquals("Activity cannot be null", exception.getMessage());
    }

    private Client createValidClient() {
        return new Client(
                "Cliente Teste",
                "cliente@teste.com.br",
                "11 98765-4321",
                "Empresa Cliente Teste",
                "Endereço do Cliente Teste"
        );
    }

    private Activity createValidActivity(Project project) {
        return new Activity(
                "Atividade Teste",
                "Descrição da atividade teste",
                project,
                LocalDate.now().plusWeeks(2),
                "Responsável Teste",
                false,
                "ALTA",
                40
        );
    }
}