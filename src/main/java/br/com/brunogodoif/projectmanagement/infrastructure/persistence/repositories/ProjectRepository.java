package br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories;

import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ClientEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, UUID> {
    List<ProjectEntity> findByIsDeletedFalse();

    List<ProjectEntity> findByStatusAndIsDeletedFalse(ProjectStatus status);

    List<ProjectEntity> findByClientAndIsDeletedFalse(ClientEntity client);
}