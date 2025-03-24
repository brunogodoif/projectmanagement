package br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories;

import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ActivityEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {
    List<ActivityEntity> findByProject(ProjectEntity project);

    List<ActivityEntity> findByProjectAndCompletedFalse(ProjectEntity project);
}