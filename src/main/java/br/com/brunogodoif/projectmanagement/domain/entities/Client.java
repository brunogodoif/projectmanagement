package br.com.brunogodoif.projectmanagement.domain.entities;

import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessValidationException;
import br.com.brunogodoif.projectmanagement.domain.utils.ValidationUtils;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
@Getter
public class Client {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;
    private List<Project> projects = new ArrayList<>();

    public Client() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.active = true;
    }

    public Client(String name, String email, String phone, String companyName, String address) {
        this();
        validateName(name);
        validateEmail(email);

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.companyName = companyName;
        this.address = address;
    }

    public Client(UUID id, String name, String email, String phone, String companyName, String address,
                  LocalDateTime createdAt, LocalDateTime updatedAt, boolean active
                 ) {
        validateName(name);
        validateEmail(email);

        this.id = (id != null) ? id : UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.companyName = companyName;
        this.address = address;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : LocalDateTime.now();
        this.active = active;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessValidationException("Client name is required");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessValidationException("Email is required");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            throw new BusinessValidationException("Invalid email format");
        }
    }

    public void addProject(Project project) {
        if (project == null) {
            throw new BusinessValidationException("Project cannot be null");
        }
        this.projects.add(project);
    }

}