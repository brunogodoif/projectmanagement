package br.com.brunogodoif.projectmanagement.infrastructure.gateways;

import br.com.brunogodoif.projectmanagement.application.gateways.ClientGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.exceptions.DatabaseOperationException;
import br.com.brunogodoif.projectmanagement.infrastructure.mappers.ClientMapper;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ClientEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ClientRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ClientGateway implements ClientGatewayInterface {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientGateway(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public Client save(Client client) {
        try {
            ClientEntity entity = clientMapper.toEntity(client);
            entity = clientRepository.save(entity);
            return clientMapper.toDomain(entity);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving client", e);
        }
    }

    @Override
    public Optional<Client> findById(UUID id) {
        try {
            return clientRepository.findById(id).map(clientMapper::toDomain);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding client by ID", e);
        }
    }

    @Override
    public List<Client> findAll() {
        try {
            List<ClientEntity> entities = clientRepository.findAll();
            return clientMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all clients", e);
        }
    }

    @Override
    public List<Client> findAllActive() {
        try {
            List<ClientEntity> entities = clientRepository.findAll();
            return clientMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding active clients", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Client ID cannot be null");
            }

            ClientEntity client = clientRepository.findById(id)
                                                  .orElseThrow(() -> new EntityNotFoundException("Client with ID " + id + " not found"));

            clientRepository.delete(client);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deactivating client with ID: " + id, e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        try {
            return clientRepository.existsByEmail(email);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error checking if email exists", e);
        }
    }
}