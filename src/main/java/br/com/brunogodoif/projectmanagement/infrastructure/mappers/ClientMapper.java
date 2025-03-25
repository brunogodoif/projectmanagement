package br.com.brunogodoif.projectmanagement.infrastructure.mappers;

import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ClientRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ClientDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ClientResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ClientEntity;
import org.mapstruct.Mapper;

import java.util.ArrayList;
import java.util.List;


@Mapper(config = MapstructBaseConfig.class)
public abstract class ClientMapper {

    public Client toDomain(ClientEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Client(entity.getId(),
                          entity.getName(),
                          entity.getEmail(),
                          entity.getPhone(),
                          entity.getCompanyName(),
                          entity.getAddress(),
                          entity.getCreatedAt(),
                          entity.getUpdatedAt(),
                          entity.isActive());
    }

    public List<Client> toDomainList(List<ClientEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }

        return entities.stream().map(this::toDomain).toList();
    }

    public ClientEntity toEntity(Client domain) {
        if (domain == null) {
            return null;
        }

        ClientEntity entity = new ClientEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setCompanyName(domain.getCompanyName());
        entity.setAddress(domain.getAddress());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        entity.setActive(domain.isActive());

        return entity;
    }

    public Client toDomain(ClientRequest request) {
        if (request == null) {
            return null;
        }

        return new Client(request.name(), request.email(), request.phone(), request.companyName(), request.address());
    }

    public abstract ClientResponse toResponse(Client domain);

    public abstract List<ClientResponse> toResponseList(List<Client> domains);

    public abstract ClientDetailResponse toDetailResponse(Client domain);

    public void updateEntityFromDomain(Client domain, ClientEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setPhone(domain.getPhone());
        entity.setCompanyName(domain.getCompanyName());
        entity.setAddress(domain.getAddress());
        entity.setActive(domain.isActive());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}