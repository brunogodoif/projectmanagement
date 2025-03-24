package br.com.brunogodoif.projectmanagement.domain.entities;

import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessValidationException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class User {
    private UUID id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private List<String> roles;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.enabled = true;
        this.roles = new ArrayList<>();
    }

    public User(String username, String password, String email, String fullName) {
        this();
        validateUsername(username);
        validatePassword(password);
        validateEmail(email);

        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    public User(UUID id, String username, String password, String email, String fullName, List<String> roles,
                boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt
               ) {
        validateUsername(username);
        validatePassword(password);
        validateEmail(email);

        this.id = (id != null) ? id : UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.roles = (roles != null) ? roles : new ArrayList<>();
        this.enabled = enabled;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : LocalDateTime.now();
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessValidationException("Username is required");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessValidationException("Password is required");
        }
        if (password.length() < 6) {
            throw new BusinessValidationException("Password must be at least 6 characters long");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessValidationException("Email is required");
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BusinessValidationException("Invalid email format");
        }
    }

    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new BusinessValidationException("Role cannot be empty");
        }
        if (!this.roles.contains(role)) {
            this.roles.add(role);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeRole(String role) {
        if (this.roles.contains(role)) {
            this.roles.remove(role);
            this.updatedAt = LocalDateTime.now();
        }
    }


    public void setUsername(String username) {
        validateUsername(username);
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
        this.updatedAt = LocalDateTime.now();
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.updatedAt = LocalDateTime.now();
    }

    public void setRoles(List<String> roles) {
        this.roles = (roles != null) ? roles : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}