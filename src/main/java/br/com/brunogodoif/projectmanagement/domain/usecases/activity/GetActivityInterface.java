package br.com.brunogodoif.projectmanagement.domain.usecases.activity;


import br.com.brunogodoif.projectmanagement.domain.entities.Activity;

import java.util.UUID;

public interface GetActivityInterface {
    Activity execute(UUID id);
}