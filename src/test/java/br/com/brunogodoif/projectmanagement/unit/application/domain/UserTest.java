package br.com.brunogodoif.projectmanagement.unit.application.domain;

import br.com.brunogodoif.projectmanagement.domain.entities.User;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("Deve criar um usuário com construtor padrão")
    void shouldCreateUserWithDefaultConstructor() {
        User user = new User();

        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.isEnabled());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Deve criar um usuário com dados básicos")
    void shouldCreateUserWithBasicData() {
        String username = "usuario.teste";
        String password = "senha123";
        String email = "usuario@teste.com.br";
        String fullName = "Usuário de Teste";

        User user = new User(username, password, email, fullName);

        assertNotNull(user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        assertTrue(user.isEnabled());
        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    @DisplayName("Deve criar um usuário com todos os parâmetros")
    void shouldCreateUserWithAllParameters() {
        UUID id = UUID.randomUUID();
        String username = "admin.sistema";
        String password = "Admin@123";
        String email = "admin@sistema.com.br";
        String fullName = "Administrador do Sistema";
        List<String> roles = Arrays.asList("ADMIN", "USER");
        boolean enabled = true;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(30);
        LocalDateTime updatedAt = LocalDateTime.now().minusDays(15);

        User user = new User(id, username, password, email, fullName, roles, enabled, createdAt, updatedAt);

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(email, user.getEmail());
        assertEquals(fullName, user.getFullName());
        assertEquals(roles, user.getRoles());
        assertEquals(enabled, user.isEnabled());
        assertEquals(createdAt, user.getCreatedAt());
        assertEquals(updatedAt, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o username for nulo")
    void shouldThrowExceptionWhenUsernameIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User(null, "senha123", "email@teste.com.br", "Nome Completo");
        });

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o username for vazio")
    void shouldThrowExceptionWhenUsernameIsEmpty() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User("", "senha123", "email@teste.com.br", "Nome Completo");
        });

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha for nula")
    void shouldThrowExceptionWhenPasswordIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User("usuario.teste", null, "email@teste.com.br", "Nome Completo");
        });

        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha for vazia")
    void shouldThrowExceptionWhenPasswordIsEmpty() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User("usuario.teste", "", "email@teste.com.br", "Nome Completo");
        });

        assertEquals("Password is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha for muito curta")
    void shouldThrowExceptionWhenPasswordIsTooShort() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User("usuario.teste", "12345", "email@teste.com.br", "Nome Completo");
        });

        assertEquals("Password must be at least 6 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email for nulo")
    void shouldThrowExceptionWhenEmailIsNull() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User("usuario.teste", "senha123", null, "Nome Completo");
        });

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email for vazio")
    void shouldThrowExceptionWhenEmailIsEmpty() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User("usuario.teste", "senha123", "", "Nome Completo");
        });

        assertEquals("Email is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o email for inválido")
    void shouldThrowExceptionWhenEmailIsInvalid() {
        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            new User("usuario.teste", "senha123", "email-invalido", "Nome Completo");
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Deve desabilitar um usuário")
    void shouldDisableUser() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        assertTrue(user.isEnabled());

        user.disable();

        assertFalse(user.isEnabled());
    }

    @Test
    @DisplayName("Deve habilitar um usuário")
    void shouldEnableUser() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        user.disable();
        assertFalse(user.isEnabled());

        user.enable();

        assertTrue(user.isEnabled());
    }

    @Test
    @DisplayName("Deve adicionar um papel ao usuário")
    void shouldAddRoleToUser() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        assertTrue(user.getRoles().isEmpty());

        user.addRole("ADMIN");

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains("ADMIN"));
    }

    @Test
    @DisplayName("Não deve adicionar papel duplicado ao usuário")
    void shouldNotAddDuplicateRoleToUser() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        user.addRole("ADMIN");
        assertEquals(1, user.getRoles().size());

        user.addRole("ADMIN");

        assertEquals(1, user.getRoles().size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar papel nulo")
    void shouldThrowExceptionWhenAddingNullRole() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            user.addRole(null);
        });

        assertEquals("Role cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar papel vazio")
    void shouldThrowExceptionWhenAddingEmptyRole() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            user.addRole("");
        });

        assertEquals("Role cannot be empty", exception.getMessage());
    }

    @Test
    @DisplayName("Deve remover um papel do usuário")
    void shouldRemoveRoleFromUser() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        user.addRole("ADMIN");
        user.addRole("USER");
        assertEquals(2, user.getRoles().size());

        user.removeRole("ADMIN");

        assertEquals(1, user.getRoles().size());
        assertFalse(user.getRoles().contains("ADMIN"));
        assertTrue(user.getRoles().contains("USER"));
    }

    @Test
    @DisplayName("Não deve fazer nada ao remover papel que não existe")
    void shouldDoNothingWhenRemovingNonExistentRole() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        user.addRole("USER");
        assertEquals(1, user.getRoles().size());

        user.removeRole("ADMIN");

        assertEquals(1, user.getRoles().size());
        assertTrue(user.getRoles().contains("USER"));
    }

    @Test
    @DisplayName("Deve atualizar o username")
    void shouldUpdateUsername() {
        User user = new User("usuario.antigo", "senha123", "email@teste.com.br", "Nome Completo");
        String newUsername = "usuario.novo";

        user.setUsername(newUsername);

        assertEquals(newUsername, user.getUsername());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar username para nulo")
    void shouldThrowExceptionWhenUpdatingUsernameToNull() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            user.setUsername(null);
        });

        assertEquals("Username is required", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar a senha")
    void shouldUpdatePassword() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        String newPassword = "NovaSenha@456";

        user.setPassword(newPassword);

        assertEquals(newPassword, user.getPassword());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar senha para uma muito curta")
    void shouldThrowExceptionWhenUpdatingPasswordToTooShort() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            user.setPassword("12345");
        });

        assertEquals("Password must be at least 6 characters long", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar o email")
    void shouldUpdateEmail() {
        User user = new User("usuario.teste", "senha123", "email.antigo@teste.com.br", "Nome Completo");
        String newEmail = "email.novo@teste.com.br";

        user.setEmail(newEmail);

        assertEquals(newEmail, user.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar email para um inválido")
    void shouldThrowExceptionWhenUpdatingEmailToInvalid() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");

        BusinessValidationException exception = assertThrows(BusinessValidationException.class, () -> {
            user.setEmail("email-invalido");
        });

        assertEquals("Invalid email format", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar o nome completo")
    void shouldUpdateFullName() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Antigo");
        String newFullName = "Nome Novo Completo";

        user.setFullName(newFullName);

        assertEquals(newFullName, user.getFullName());
    }

    @Test
    @DisplayName("Deve atualizar a lista de papéis")
    void shouldUpdateRoles() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        user.addRole("USER");
        List<String> newRoles = Arrays.asList("ADMIN", "MANAGER");

        user.setRoles(newRoles);

        assertEquals(newRoles, user.getRoles());
    }

    @Test
    @DisplayName("Deve definir lista vazia quando atualizar papéis para nulo")
    void shouldSetEmptyListWhenUpdatingRolesToNull() {
        User user = new User("usuario.teste", "senha123", "email@teste.com.br", "Nome Completo");
        user.addRole("ADMIN");
        assertFalse(user.getRoles().isEmpty());

        user.setRoles(null);

        assertNotNull(user.getRoles());
        assertTrue(user.getRoles().isEmpty());
    }
}