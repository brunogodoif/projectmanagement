package br.com.brunogodoif.projectmanagement.domain.entities;

import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Project {
    private UUID id;
    private String name;
    private String description;
    private Client client;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
    private String manager;
    private String notes;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Activity> activities = new ArrayList<>();

    public Project() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
    }

    public Project(String name, String description, Client client, LocalDate startDate, LocalDate endDate,
                   ProjectStatus status, String manager, String notes
                  ) {
        this();
        validateName(name);
        validateClient(client);
        validateStatus(status);
        validateDates(startDate, endDate);

        this.name = name;
        this.description = description;
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.manager = manager;
        this.notes = notes;
    }

    public Project(UUID id, String name, String description, Client client, LocalDate startDate, LocalDate endDate,
                   ProjectStatus status, String manager, String notes, boolean isDeleted, LocalDateTime createdAt,
                   LocalDateTime updatedAt
                  ) {
        validateName(name);
        validateClient(client);
        validateStatus(status);
        validateDates(startDate, endDate);

        this.id = (id != null) ? id : UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.client = client;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.manager = manager;
        this.notes = notes;
        this.isDeleted = isDeleted;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : LocalDateTime.now();
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessValidationException("Project name is required");
        }
    }

    private void validateClient(Client client) {
        if (client == null) {
            throw new BusinessValidationException("Client is required");
        }
        if (client.getId() == null) {
            throw new BusinessValidationException("Client ID is required");
        }
    }

    private void validateStatus(ProjectStatus status) {
        if (status == null) {
            throw new BusinessValidationException("Project status is required");
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BusinessValidationException("End date cannot be before start date");
        }
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(ProjectStatus newStatus) {
        validateStatus(newStatus);
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }

    public void addActivity(Activity activity) {
        if (activity == null) {
            throw new BusinessValidationException("Activity cannot be null");
        }
        this.activities.add(activity);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = (id != null) ? id : UUID.randomUUID();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        validateClient(client);
        this.client = client;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        validateDates(startDate, this.endDate);
        this.startDate = startDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        validateDates(this.startDate, endDate);
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        validateStatus(status);
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
        this.updatedAt = LocalDateTime.now();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = (activities != null) ? activities : new ArrayList<>();
    }
}