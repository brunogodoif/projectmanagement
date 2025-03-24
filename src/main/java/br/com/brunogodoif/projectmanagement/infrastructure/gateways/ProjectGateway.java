package br.com.brunogodoif.projectmanagement.infrastructure.gateways;

import br.com.brunogodoif.projectmanagement.application.gateways.ProjectGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.infrastructure.gateways.exceptions.DatabaseOperationException;
import br.com.brunogodoif.projectmanagement.infrastructure.mappers.ProjectMapper;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ClientEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ProjectEntity;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ClientRepository;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ProjectRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectGateway implements ProjectGatewayInterface {

    private final ProjectRepository projectRepository;
    private final ClientRepository clientRepository;
    private final ProjectMapper projectMapper;

    public ProjectGateway(ProjectRepository projectRepository, ClientRepository clientRepository,
                          ProjectMapper projectMapper
                         ) {
        this.projectRepository = projectRepository;
        this.clientRepository = clientRepository;
        this.projectMapper = projectMapper;
    }

    @Override
    public Project save(Project project) {
        try {
            ProjectEntity entity = projectMapper.toEntity(project);

            if (project.getClient() != null && project.getClient().getId() != null) {
                ClientEntity clientEntity = clientRepository.findById(project.getClient().getId())
                                                            .orElseThrow(() -> new IllegalArgumentException(
                                                                    "Client not found"));
                entity.setClient(clientEntity);
            }

            entity = projectRepository.save(entity);
            return projectMapper.toDomain(entity);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error saving project", e);
        }
    }

    @Override
    public Optional<Project> findById(UUID id) {
        try {
            return projectRepository.findById(id).map(projectMapper::toDomain);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding project by ID", e);
        }
    }

    @Override
    public List<Project> findAll() {
        try {
            List<ProjectEntity> entities = projectRepository.findAll();
            return projectMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding all projects", e);
        }
    }

    @Override
    public List<Project> findAllActive() {
        try {
            List<ProjectEntity> entities = projectRepository.findByIsDeletedFalse();
            return projectMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding active projects", e);
        }
    }

    @Override
    public List<Project> findByStatus(ProjectStatus status) {
        try {
            List<ProjectEntity> entities = projectRepository.findByStatusAndIsDeletedFalse(status);
            return projectMapper.toDomainList(entities);
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding projects by status", e);
        }
    }

    @Override
    public List<Project> findByClientId(UUID clientId) {
        try {
            Optional<ClientEntity> clientEntity = clientRepository.findById(clientId);
            if (clientEntity.isPresent()) {
                List<ProjectEntity> entities = projectRepository.findByClientAndIsDeletedFalse(clientEntity.get());
                return projectMapper.toDomainList(entities);
            }
            return List.of();
        } catch (Exception e) {
            throw new DatabaseOperationException("Error finding projects by client", e);
        }
    }

    @Override
    public void deleteById(UUID id) {
        try {
            if (id == null) {
                throw new IllegalArgumentException("Project ID cannot be null");
            }

            ProjectEntity project = projectRepository.findById(id)
                                                     .orElseThrow(() -> new EntityNotFoundException("Project with ID " + id + " not found"));

            project.setDeleted(true);
            projectRepository.save(project);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseOperationException("Error deleting project with ID: " + id, e);
        }
    }
}
