package br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories;

import br.com.brunogodoif.projectmanagement.infrastructure.persistence.entities.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {

    List<ClientEntity> findByActiveTrue();

    boolean existsByEmail(String email);
}