package br.com.brunogodoif.projectmanagement.domain.entities;

import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessValidationException;
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
            throw new BusinessValidationException("Client email is required");
        }
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new BusinessValidationException("Invalid email format");
        }
    }

    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void addProject(Project project) {
        if (project == null) {
            throw new BusinessValidationException("Project cannot be null");
        }
        this.projects.add(project);
    }

    public void setId(UUID id) {
        this.id = (id != null) ? id : UUID.randomUUID();
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = LocalDateTime.now();
    }


    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void setProjects(List<Project> projects) {
        this.projects = (projects != null) ? projects : new ArrayList<>();
    }
}