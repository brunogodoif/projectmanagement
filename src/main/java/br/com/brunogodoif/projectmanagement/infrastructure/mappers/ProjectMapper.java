package br.com.brunogodoif.projectmanagement.infrastructure.mappers;

import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ProjectRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ProjectDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ProjectResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ProjectEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@Mapper(config = MapstructBaseConfig.class)
public abstract class ProjectMapper {

    protected ClientMapper clientMapper;

    @Autowired
    protected void setClientMapper(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    public Project toDomain(ProjectEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Project(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                clientMapper.toDomain(entity.getClient()),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getManager(),
                entity.getNotes(),
                entity.isDeleted(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public List<Project> toDomainList(List<ProjectEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream()
                       .map(this::toDomain)
                       .toList();
    }

    public ProjectEntity toEntity(Project domain) {
        if (domain == null) {
            return null;
        }

        ProjectEntity entity = new ProjectEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(domain.getStatus());
        entity.setManager(domain.getManager());
        entity.setNotes(domain.getNotes());
        entity.setDeleted(domain.isDeleted());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());

        if (domain.getClient() != null) {
            entity.setClient(clientMapper.toEntity(domain.getClient()));
        }

        return entity;
    }

    public Project toDomain(ProjectRequest request) {
        if (request == null) {
            return null;
        }

        return new Project(
                request.name(),
                request.description(),
                null,
                request.startDate(),
                request.endDate(),
                request.status(),
                request.manager(),
                request.notes()
        );
    }

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    public abstract ProjectResponse toResponse(Project domain);

    public abstract List<ProjectResponse> toResponseList(List<Project> domains);

    public abstract ProjectDetailResponse toDetailResponse(Project domain);

    public void updateEntityFromDomain(Project domain, ProjectEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        entity.setStartDate(domain.getStartDate());
        entity.setEndDate(domain.getEndDate());
        entity.setStatus(domain.getStatus());
        entity.setManager(domain.getManager());
        entity.setNotes(domain.getNotes());
        entity.setDeleted(domain.isDeleted());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}