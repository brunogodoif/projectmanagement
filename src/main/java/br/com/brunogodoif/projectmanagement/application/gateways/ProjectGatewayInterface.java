package br.com.brunogodoif.projectmanagement.application.gateways;

import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectGatewayInterface {
    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findAll();

    List<Project> findAllActive();

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByClientId(UUID clientId);

    void deleteById(UUID id);
}