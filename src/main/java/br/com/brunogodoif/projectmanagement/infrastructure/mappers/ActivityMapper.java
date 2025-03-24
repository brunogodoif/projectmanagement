package br.com.brunogodoif.projectmanagement.infrastructure.mappers;

import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ActivityRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ActivityDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ActivityResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ActivityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapstructBaseConfig.class, uses = {ProjectMapper.class})
public interface ActivityMapper {

    @Mapping(target = "project", source = "project")
    Activity toDomain(ActivityEntity entity);

    @Mapping(target = "project", source = "project")
    ActivityEntity toEntity(Activity domain);

    List<Activity> toDomainList(List<ActivityEntity> entities);

    @Mapping(target = "project", ignore = true)
    Activity toDomain(ActivityRequest request);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    ActivityResponse toResponse(Activity domain);

    List<ActivityResponse> toResponseList(List<Activity> domains);

    ActivityDetailResponse toDetailResponse(Activity domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    void updateEntityFromDomain(Activity domain, @MappingTarget ActivityEntity entity);
}
