package br.com.brunogodoif.projectmanagement.domain.usecases.activity;

import br.com.brunogodoif.projectmanagement.domain.dtos.ActivityInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;

import java.util.UUID;

public interface UpdateActivityInterface {
    Activity execute(UUID id, ActivityInputDTO activityInputDTO);
}