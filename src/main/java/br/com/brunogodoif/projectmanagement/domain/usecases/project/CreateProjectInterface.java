package br.com.brunogodoif.projectmanagement.domain.usecases.project;

import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;

public interface CreateProjectInterface {
    Project execute(ProjectInputDTO projectInputDTO);
}
