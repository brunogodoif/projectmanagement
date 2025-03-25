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

class ActivityTest {

    @Test
    @DisplayName("Deve criar uma atividade com construtor padrão")
    void shouldCreateActivityWithDefaultConstructor() {
        Activity activity = new Activity();

        assertNotNull(activity.getId());
        assertNotNull(activity.getCreatedAt());
        assertNotNull(activity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar uma atividade com dados básicos")
    void shouldCreateActivityWithBasicData() {
        String title = "Implementar Autenticação";
        String description = "Implementar autenticação com JWT";
        Project project = createValidProject();
        LocalDate dueDate = LocalDate.now().plusWeeks(2);
        String assignedTo = "Carlos Programador";
        boolean completed = false;
        String priority = "ALTA";
        int estimatedHours = 16;

        Activity activity = new Activity(title, description, project, dueDate, assignedTo, completed, priority, estimatedHours);

        assertNotNull(activity.getId());
        assertEquals(title, activity.getTitle());
        assertEquals(description, activity.getDescription());
        assertEquals(project, activity.getProject());
        assertEquals(dueDate, activity.getDueDate());
        assertEquals(assignedTo, activity.getAssignedTo());
        assertEquals(completed, activity.isCompleted());
        assertEquals(priority, activity.getPriority());
        assertEquals(estimatedHours, activity.getEstimatedHours());
        assertNotNull(activity.getCreatedAt());
        assertNotNull(activity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar uma atividade com todos os parâmetros")
    void shouldCreateActivityWithAllParameters() {
        UUID id = UUID.randomUUID();
        String title = "Desenvolver Layout da Página Inicial";
        String description = "Criar o layout responsivo da página inicial seguindo o design";
        Project project = createValidProject();
        LocalDate dueDate = LocalDate.now().plusWeeks(1);
        String assignedTo = "Ana Designer";
        boolean completed = true;
        String priority = "MÉDIA";
        int estimatedHours = 8;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(15);
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(2);

        Activity activity = new Activity(id, title, description, project, dueDate, assignedTo, completed, priority, estimatedHours, createdAt, updatedAt);

        assertEquals(id, activity.getId());
        assertEquals(title, activity.getTitle());
        assertEquals(description, activity.getDescription());
        assertEquals(project, activity.getProject());
        assertEquals(dueDate, activity.getDueDate());
        assertEquals(assignedTo, activity.getAssignedTo());
        assertEquals(completed, activity.isCompleted());
        assertEquals(priority, activity.getPriority());
        assertEquals(estimatedHours, activity.getEstimatedHours());
        assertEquals(createdAt, activity.getCreatedAt());
        assertEquals(updatedAt, activity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o título for nulo")
    void shouldThrowExceptionWhenTitleIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Activity(
                    null,
                    "Descrição da atividade",
                    createValidProject(),
                    LocalDate.now().plusWeeks(1),
                    "Responsável Teste",
                    false,
                    "ALTA",
                    24
            );
        });

        assertEquals("Activity title is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o título for vazio")
    void shouldThrowExceptionWhenTitleIsEmpty() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Activity(
                    "",
                    "Descrição da atividade",
                    createValidProject(),
                    LocalDate.now().plusWeeks(1),
                    "Responsável Teste",
                    false,
                    "ALTA",
                    24
            );
        });

        assertEquals("Activity title is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o projeto for nulo")
    void shouldThrowExceptionWhenProjectIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Activity(
                    "Título da Atividade",
                    "Descrição da atividade",
                    null,
                    LocalDate.now().plusWeeks(1),
                    "Responsável Teste",
                    false,
                    "ALTA",
                    24
            );
        });

        assertEquals("Project is required", exception.getMessage());
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

    private Project createValidProject() {
        return new Project(
                "Projeto Teste",
                "Descrição do projeto teste",
                createValidClient(),
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                ProjectStatus.OPEN,
                "Gerente Teste",
                "Notas do projeto"
        );
    }

}