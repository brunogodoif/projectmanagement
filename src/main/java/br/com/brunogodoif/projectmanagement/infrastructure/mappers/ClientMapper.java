package br.com.brunogodoif.projectmanagement.infrastructure.mappers;

import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ClientRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ClientDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ClientResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ClientEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(config = MapstructBaseConfig.class)
public interface ClientMapper {

    @Mapping(target = "projects", ignore = true)
    Client toDomain(ClientEntity entity);

    @Mapping(target = "projects", ignore = true)
    ClientEntity toEntity(Client domain);

    List<Client> toDomainList(List<ClientEntity> entities);

    Client toDomain(ClientRequest request);

    ClientResponse toResponse(Client domain);

    List<ClientResponse> toResponseList(List<Client> domains);

    ClientDetailResponse toDetailResponse(Client domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDomain(Client domain, @MappingTarget ClientEntity entity);
}