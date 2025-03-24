package br.com.brunogodoif.projectmanagement.domain.usecases.project;

import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;

import java.util.List;
import java.util.UUID;

public interface ListProjectsInterface {
    List<Project> execute();

    List<Project> executeByStatus(ProjectStatus status);

    List<Project> executeByClient(UUID clientId);
}