package br.com.brunogodoif.projectmanagement.unit.application.domain;

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

class ClientTest {

    @Test
    @DisplayName("Deve criar um cliente com construtor padrão")
    void shouldCreateClientWithDefaultConstructor() {
        Client client = new Client();

        assertNotNull(client.getId());
        assertNotNull(client.getCreatedAt());
        assertNotNull(client.getUpdatedAt());
        assertTrue(client.isActive());
        assertTrue(client.getProjects().isEmpty());
    }

    @Test
    @DisplayName("Deve criar um cliente com dados básicos")
    void shouldCreateClientWithBasicData() {
        String name = "Empresa ABC";
        String email = "contato@empresaabc.com.br";
        String phone = "11 98765-4321";
        String companyName = "ABC Ltda";
        String address = "Av. Paulista, 1000, São Paulo, SP";

        Client client = new Client(name, email, phone, companyName, address);

        assertNotNull(client.getId());
        assertEquals(name, client.getName());
        assertEquals(email, client.getEmail());
        assertEquals(phone, client.getPhone());
        assertEquals(companyName, client.getCompanyName());
        assertEquals(address, client.getAddress());
        assertNotNull(client.getCreatedAt());
        assertNotNull(client.getUpdatedAt());
        assertTrue(client.isActive());
        assertTrue(client.getProjects().isEmpty());
    }

    @Test
    @DisplayName("Deve criar um cliente com todos os parâmetros")
    void shouldCreateClientWithAllParameters() {
        UUID id = UUID.randomUUID();
        String name = "Empresa XYZ";
        String email = "contato@empresaxyz.com.br";
        String phone = "11 91234-5678";
        String companyName = "XYZ S.A.";
        String address = "Rua Augusta, 500, São Paulo, SP";
        LocalDateTime createdAt = LocalDateTime.now().minusDays(30);
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(15);
        boolean active = true;

        Client client = new Client(id, name, email, phone, companyName, address, createdAt, updatedAt, active);

        assertEquals(id, client.getId());
        assertEquals(name, client.getName());
        assertEquals(email, client.getEmail());
        assertEquals(phone, client.getPhone());
        assertEquals(companyName, client.getCompanyName());
        assertEquals(address, client.getAddress());
        assertEquals(createdAt, client.getCreatedAt());
        assertEquals(updatedAt, client.getUpdatedAt());
        assertEquals(active, client.isActive());
        assertTrue(client.getProjects().isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome for nulo")
    void shouldThrowExceptionWhenNameIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Client(null, "email@teste.com.br", "11 98765-4321", "Empresa Teste", "Endereço Teste");
        });

        assertEquals("Client name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nome for vazio")
    void shouldThrowExceptionWhenNameIsEmpty() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Client("", "email@teste.com.br", "11 98765-4321", "Empresa Teste", "Endereço Teste");
        });

        assertEquals("Client name is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email for nulo")
    void shouldThrowExceptionWhenEmailIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Client("Nome Teste", null, "11 98765-4321", "Empresa Teste", "Endereço Teste");
        });

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email for vazio")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Client("Nome Teste", "", "11 98765-4321", "Empresa Teste", "Endereço Teste");
        });

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email for inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new Client("Nome Teste", "email-invalido", "11 98765-4321", "Empresa Teste", "Endereço Teste");
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar projeto ao cliente")
    void shouldAddProjectToClient() {
        Client client = new Client("Nome Teste", "email@teste.com.br", "11 98765-4321", "Empresa Teste", "Endereço Teste");
        Project project = createValidProject(client);

        client.addProject(project);

        assertEquals(1, client.getProjects().size());
        assertTrue(client.getProjects().contains(project));
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar projeto nulo")
    void shouldThrowExceptionWhenAddingNullProject() {
        Client client = new Client("Nome Teste", "email@teste.com.br", "11 98765-4321", "Empresa Teste", "Endereço Teste");

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            client.addProject(null);
        });

        assertEquals("Project cannot be null", exception.getMessage());
    }

    private Project createValidProject(Client client) {
        return new Project(
                "Projeto Teste",
                "Descrição do projeto teste",
                client,
                LocalDate.now(),
                LocalDate.now().plusMonths(3),
                ProjectStatus.OPEN,
                "Gerente Teste",
                "Notas do projeto"
        );
    }
}