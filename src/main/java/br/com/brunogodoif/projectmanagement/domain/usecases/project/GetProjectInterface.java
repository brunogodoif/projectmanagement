package br.com.brunogodoif.projectmanagement.domain.usecases.project;

import br.com.brunogodoif.projectmanagement.domain.entities.Project;

import java.util.UUID;

public interface GetProjectInterface {
    Project execute(UUID id);
}
