package br.com.brunogodoif.projectmanagement.infrastructure.controllers;

import br.com.brunogodoif.projectmanagement.application.usecases.activity.DeleteActivityUseCase;
import br.com.brunogodoif.projectmanagement.domain.entities.Activity;
import br.com.brunogodoif.projectmanagement.domain.usecases.DeleteEntityInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.CreateActivityInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.GetActivityInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.ListActivitiesByProjectInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.activity.UpdateActivityInterface;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ActivityRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ActivityResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.mappers.ActivityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activities", description = "Activity management endpoints")
public class ActivityController {

    private final CreateActivityInterface createActivityUseCase;
    private final GetActivityInterface getActivityUseCase;
    private final ListActivitiesByProjectInterface listActivitiesByProjectUseCase;
    private final UpdateActivityInterface updateActivityUseCase;
    private final DeleteEntityInterface<UUID> deleteActivity;
    private final ActivityMapper activityMapper;

    public ActivityController(CreateActivityInterface createActivityUseCase, GetActivityInterface getActivityUseCase,
                              ListActivitiesByProjectInterface listActivitiesByProjectUseCase,
                              UpdateActivityInterface updateActivityUseCase, DeleteActivityUseCase deleteActivity,
                              ActivityMapper activityMapper
                             ) {
        this.createActivityUseCase = createActivityUseCase;
        this.getActivityUseCase = getActivityUseCase;
        this.listActivitiesByProjectUseCase = listActivitiesByProjectUseCase;
        this.updateActivityUseCase = updateActivityUseCase;
        this.deleteActivity = deleteActivity;
        this.activityMapper = activityMapper;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Create a new activity")
    public ResponseEntity<ActivityResponse> createActivity(@Valid @RequestBody ActivityRequest request) {
        Activity activity = activityMapper.toDomain(request);

        if (request.projectId() != null) {
            activity.setProject(new br.com.brunogodoif.projectmanagement.domain.entities.Project());
            activity.getProject().setId(request.projectId());
        }

        Activity createdActivity = createActivityUseCase.execute(activity);
        return new ResponseEntity<>(activityMapper.toResponse(createdActivity), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get activity by ID")
    public ResponseEntity<ActivityResponse> getActivity(@PathVariable UUID id) {
        Activity activity = getActivityUseCase.execute(id);
        return ResponseEntity.ok(activityMapper.toResponse(activity));
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "List all activities by project")
    public ResponseEntity<List<ActivityResponse>> listActivitiesByProject(@PathVariable UUID projectId) {
        List<Activity> activities = listActivitiesByProjectUseCase.execute(projectId);
        return ResponseEntity.ok(activityMapper.toResponseList(activities));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Update an existing activity")
    public ResponseEntity<ActivityResponse> updateActivity(@PathVariable UUID id,
                                                           @Valid @RequestBody ActivityRequest request
                                                          ) {
        Activity activity = activityMapper.toDomain(request);

        if (request.projectId() != null) {
            activity.setProject(new br.com.brunogodoif.projectmanagement.domain.entities.Project());
            activity.getProject().setId(request.projectId());
        }

        Activity updatedActivity = updateActivityUseCase.execute(id, activity);
        return ResponseEntity.ok(activityMapper.toResponse(updatedActivity));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Delete an activity")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        deleteActivity.execute(id);
        return ResponseEntity.noContent().build();
    }
}