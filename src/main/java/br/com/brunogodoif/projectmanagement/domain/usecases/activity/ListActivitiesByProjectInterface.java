package br.com.brunogodoif.projectmanagement.domain.usecases.activity;

import br.com.brunogodoif.projectmanagement.domain.entities.Activity;

import java.util.List;
import java.util.UUID;

public interface ListActivitiesByProjectInterface {
    List<Activity> execute(UUID projectId);
}
