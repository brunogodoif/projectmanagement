package br.com.brunogodoif.projectmanagement.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.exceptions.BusinessOperationException;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import br.com.brunogodoif.projectmanagement.domain.usecases.DeleteEntityInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class DeleteActivityUseCase implements DeleteEntityInterface<UUID> {

    private final ActivityGatewayInterface activityGateway;

    public DeleteActivityUseCase(ActivityGatewayInterface activityGateway) {
        this.activityGateway = activityGateway;
    }

    @Override
    public void execute(UUID id) {
        log.info("Deleting activity with ID: {}", id);

        try {
            Activity activity = activityGateway.findById(id).orElseThrow(() -> new EntityNotFoundException(
                    "Activity not found with ID: " + id));

            activityGateway.deleteById(id);
            log.info("Activity with ID: {} successfully deleted", id);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessOperationException("Failed to delete activity", e);
        }
    }
}