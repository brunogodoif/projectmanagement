package br.com.brunogodoif.projectmanagement.infrastructure.mappers;

import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ActivityRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ActivityDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ActivityResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ActivityEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@Mapper(config = MapstructBaseConfig.class)
public abstract class ActivityMapper {

    protected ProjectMapper projectMapper;

    @Autowired
    public void setProjectMapper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public Activity toDomain(ActivityEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Activity(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                projectMapper.toDomain(entity.getProject()),
                entity.getDueDate(),
                entity.getAssignedTo(),
                entity.isCompleted(),
                entity.getPriority(),
                entity.getEstimatedHours(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<Activity> toDomainList(List<ActivityEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                       .map(this::toDomain)
                       .toList();
    }

    public ActivityEntity toEntity(Activity domain) {
        if (domain == null) {
            return null;
        }

        ActivityEntity entity = new ActivityEntity();
        entity.setId(domain.getId());
        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setDueDate(domain.getDueDate());
        entity.setAssignedTo(domain.getAssignedTo());
        entity.setCompleted(domain.isCompleted());
        entity.setPriority(domain.getPriority());
        entity.setEstimatedHours(domain.getEstimatedHours());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getProject() != null) {
            entity.setProject(projectMapper.toEntity(domain.getProject()));
        }

        return entity;
    }

    public Activity toDomain(ActivityRequest request) {
        if (request == null) {
            return null;
        }

        return new Activity(
                request.title(),
                request.description(),
                null,
                request.dueDate(),
                request.assignedTo(),
                request.completed(),
                request.priority(),
                request.estimatedHours()
        );
    }

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    public abstract ActivityResponse toResponse(Activity domain);

    public abstract List<ActivityResponse> toResponseList(List<Activity> domains);

    public abstract ActivityDetailResponse toDetailResponse(Activity domain);

    public void updateEntityFromDomain(Activity domain, ActivityEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setDueDate(domain.getDueDate());
        entity.setAssignedTo(domain.getAssignedTo());
        entity.setCompleted(domain.isCompleted());
        entity.setPriority(domain.getPriority());
        entity.setEstimatedHours(domain.getEstimatedHours());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}