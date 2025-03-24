package br.com.brunogodoif.projectmanagement.application.usecases.activity;

import br.com.brunogodoif.projectmanagement.application.gateways.ActivityGatewayInterface;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.GetActivityInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Log4j2
public class GetActivityUseCase implements GetActivityInterface {

    private final ActivityGatewayInterface activityGateway;

    public GetActivityUseCase(ActivityGatewayInterface activityGateway) {
        this.activityGateway = activityGateway;
    }

    @Override
    public Activity execute(UUID id) {
        log.info("Getting activity with ID: {}", id);

        return activityGateway.findById(id)
                              .orElseThrow(() -> new IllegalArgumentException("Activity not found with ID: " + id));
    }
}
