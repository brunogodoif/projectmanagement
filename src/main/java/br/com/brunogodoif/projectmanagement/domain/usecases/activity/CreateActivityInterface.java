package br.com.brunogodoif.projectmanagement.domain.usecases.activity;

import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;

public interface CreateActivityInterface {
    Activity execute(ActivityInputDTO activityInputDTO);
}