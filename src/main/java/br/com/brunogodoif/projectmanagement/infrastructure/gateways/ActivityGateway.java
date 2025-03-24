package br.com.brunogodoif.projectmanagement.infrastructure.gateways;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.exceptions.DatabaseOperationException;
import br.com.brunogodoif.projectmanagement.infrastructure.mappers.ActivityMapper;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ActivityEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ProjectEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ActivityRepository;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ActivityGateway implements ActivityGatewayInterface {

    private final ActivityRepository activityRepository;
    private final ProjectRepository projectRepository;
    private final ActivityMapper activityMapper;

    public ActivityGateway(ActivityRepository activityRepository, ProjectRepository projectRepository,
                           ActivityMapper activityMapper
                          ) {
        this.activityRepository = activityRepository;
        this.projectRepository = projectRepository;
        this.activityMapper = activityMapper;
    }

    @Override
    public Activity save(Activity activity) {
        try {
            ActivityEntity entity = activityMapper.toEntity(activity);

            if (activity.getProject() != null && activity.getProject().getId() != null) {
                ProjectEntity projectEntity = projectRepository.findById(activity.getProject().getId())
                                                               .orElseThrow(() -> new IllegalArgumentException(
                                                                       "Project not found"));
                entity.setProject(projectEntity);
            }

            entity = activityRepository.save(entity);
            return activityMapper.toDomain(entity);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving activity", e);
        }
    }

    @Override
    public Optional<Activity> findById(UUID id) {
        try {
            return activityRepository.findById(id).map(activityMapper::toDomain);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding activity by ID", e);
        }
    }

    @Override
    public List<Activity> findAll() {
        try {
            List<ActivityEntity> entities = activityRepository.findAll();
            return activityMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all activities", e);
        }
    }

    @Override
    public List<Activity> findByProjectId(UUID projectId) {
        try {
            Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
            if (projectEntity.isPresent()) {
                List<ActivityEntity> entities = activityRepository.findByProject(projectEntity.get());
                return activityMapper.toDomainList(entities);
            }
            return List.of();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding activities by project", e);
        }
    }

    @Override
    public List<Activity> findPendingByProjectId(UUID projectId) {
        try {
            Optional<ProjectEntity> projectEntity = projectRepository.findById(projectId);
            if (projectEntity.isPresent()) {
                List<ActivityEntity> entities = activityRepository.findByProjectAndCompletedFalse(projectEntity.get());
                return activityMapper.toDomainList(entities);
            }
            return List.of();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding pending activities by project", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Activity ID cannot be null");
            }

            if (!activityRepository.existsById(id)) {
                throw new EntityNotFoundException("Activity with ID " + id + " not found");
            }

            activityRepository.deleteById(id);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting activity with ID: " + id, e);
        }
    }
}
