package br.com.brunogodoif.projectmanagement.infrastructure.mappers;

import br.com.brunogodoif.projectmanagement.domain.entities.User;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapstructBaseConfig.class)
public interface UserMapper {

    User toDomain(UserEntity entity);

    UserEntity toEntity(User domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateEntityFromDomain(User domain, @MappingTarget UserEntity entity);
}