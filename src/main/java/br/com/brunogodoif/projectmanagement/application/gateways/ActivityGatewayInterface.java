package br.com.brunogodoif.projectmanagement.application.gateways;

import br.com.brunogodoif.projectmanagement.domain.entities.Activity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityGatewayInterface {
    Activity save(Activity activity);

    Optional<Activity> findById(UUID id);

    List<Activity> findAll();

    List<Activity> findByProjectId(UUID projectId);

    List<Activity> findPendingByProjectId(UUID projectId);

    void deleteById(UUID id);
}