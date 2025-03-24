package br.com.brunogodoif.projectmanagement.infrastructure.mappers;

import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ProjectRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ProjectDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ProjectResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapstructBaseConfig.class, uses = {ClientMapper.class})
public interface ProjectMapper {

    @Mapping(target = "client", source = "client")
    Project toDomain(ProjectEntity entity);

    @Mapping(target = "client", source = "client")
    ProjectEntity toEntity(Project domain);

    List<Project> toDomainList(List<ProjectEntity> entities);

    @Mapping(target = "client", ignore = true)
    Project toDomain(ProjectRequest request);

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "clientName", source = "client.name")
    ProjectResponse toResponse(Project domain);

    List<ProjectResponse> toResponseList(List<Project> domains);

    ProjectDetailResponse toDetailResponse(Project domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "client", ignore = true)
    void updateEntityFromDomain(Project domain, @MappingTarget ProjectEntity entity);
}