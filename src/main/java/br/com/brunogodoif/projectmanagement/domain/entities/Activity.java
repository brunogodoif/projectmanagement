package br.com.brunogodoif.projectmanagement.domain.entities;

import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Activity {
    private UUID id;
    private String title;
    private String description;
    private Project project;
    private LocalDate dueDate;
    private String assignedTo;
    private boolean completed;
    private String priority;
    private int estimatedHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Activity() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Activity(String title, String description, Project project, LocalDate dueDate, String assignedTo,
                    boolean completed, String priority, int estimatedHours
                   ) {
        this();
        validateTitle(title);
        validateProject(project);

        this.title = title;
        this.description = description;
        this.project = project;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.completed = completed;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
    }

    public Activity(UUID id, String title, String description, Project project, LocalDate dueDate, String assignedTo,
                    boolean completed, String priority, int estimatedHours, LocalDateTime createdAt,
                    LocalDateTime updatedAt
                   ) {
        validateTitle(title);
        validateProject(project);

        this.id = (id != null) ? id : UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.project = project;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.completed = completed;
        this.priority = priority;
        this.estimatedHours = estimatedHours;
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();
        this.updatedAt = (updatedAt != null) ? updatedAt : LocalDateTime.now();
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new BusinessValidationException("Activity title is required");
        }
    }

    private void validateProject(Project project) {
        if (project == null) {
            throw new BusinessValidationException("Project is required");
        }
        if (project.getId() == null) {
            throw new BusinessValidationException("Project ID is required");
        }
    }

    public void markAsCompleted() {
        this.completed = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(boolean completed) {
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
    }

    public void reassign(String assignedTo) {
        if (assignedTo == null || assignedTo.trim().isEmpty()) {
            throw new BusinessValidationException("Assigned person is required");
        }
        this.assignedTo = assignedTo;
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = (id != null) ? id : UUID.randomUUID();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        validateTitle(title);
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        validateProject(project);
        this.project = project;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        this.updatedAt = LocalDateTime.now();
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        this.updatedAt = LocalDateTime.now();
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
        this.updatedAt = LocalDateTime.now();
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        if (estimatedHours < 0) {
            throw new BusinessValidationException("Estimated hours cannot be negative");
        }
        this.estimatedHours = estimatedHours;
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
}