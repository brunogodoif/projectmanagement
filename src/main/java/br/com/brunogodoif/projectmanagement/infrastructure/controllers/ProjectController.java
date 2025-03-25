package br.com.brunogodoif.projectmanagement.infrastructure.controllers;

import br.com.brunogodoif.projectmanagement.application.usecases.project.DeleteProjectUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.usecases.DeleteEntityInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.CreateProjectInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.GetProjectInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.ListProjectsInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.project.UpdateProjectInterface;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ProjectRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ProjectDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ProjectResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.mappers.ProjectMapper;
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
@RequestMapping("/api/projects")
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final CreateProjectInterface createProjectUseCase;
    private final GetProjectInterface getProjectUseCase;
    private final ListProjectsInterface listProjectsUseCase;
    private final UpdateProjectInterface updateProjectUseCase;
    private final DeleteEntityInterface<UUID> deleteProject;
    private final ProjectMapper projectMapper;

    public ProjectController(CreateProjectInterface createProjectUseCase, GetProjectInterface getProjectUseCase,
                             ListProjectsInterface listProjectsUseCase, UpdateProjectInterface updateProjectUseCase,
                             DeleteProjectUseCase deleteProject, ProjectMapper projectMapper
                            ) {
        this.createProjectUseCase = createProjectUseCase;
        this.getProjectUseCase = getProjectUseCase;
        this.listProjectsUseCase = listProjectsUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProject = deleteProject;
        this.projectMapper = projectMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {

        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder()
                                                         .id(UUID.randomUUID())
                                                         .name(request.name())
                                                         .description(request.description())
                                                         .clientId(request.clientId())
                                                         .startDate(request.startDate())
                                                         .endDate(request.endDate())
                                                         .status(request.status())
                                                         .manager(request.manager())
                                                         .notes(request.notes())
                                                         .build();

        Project createdProject = createProjectUseCase.execute(projectInputDTO);
        return new ResponseEntity<>(projectMapper.toResponse(createdProject), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get project by ID with associated activities")
    public ResponseEntity<ProjectDetailResponse> getProject(@PathVariable UUID id) {
        Project project = getProjectUseCase.execute(id);
        return ResponseEntity.ok(projectMapper.toDetailResponse(project));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "List all active projects")
    public ResponseEntity<List<ProjectResponse>> listProjects() {
        List<Project> projects = listProjectsUseCase.execute();
        return ResponseEntity.ok(projectMapper.toResponseList(projects));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "List all active projects by status")
    public ResponseEntity<List<ProjectResponse>> listProjectsByStatus(@PathVariable ProjectStatus status) {
        List<Project> projects = listProjectsUseCase.executeByStatus(status);
        return ResponseEntity.ok(projectMapper.toResponseList(projects));
    }

    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "List all active projects by client")
    public ResponseEntity<List<ProjectResponse>> listProjectsByClient(@PathVariable UUID clientId) {
        List<Project> projects = listProjectsUseCase.executeByClient(clientId);
        return ResponseEntity.ok(projectMapper.toResponseList(projects));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing project")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable UUID id,
                                                         @Valid @RequestBody ProjectRequest request
                                                        ) {

        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder()
                                                         .name(request.name())
                                                         .description(request.description())
                                                         .clientId(request.clientId())
                                                         .startDate(request.startDate())
                                                         .endDate(request.endDate())
                                                         .status(request.status())
                                                         .manager(request.manager())
                                                         .notes(request.notes())
                                                         .build();

        Project updatedProject = updateProjectUseCase.execute(id, projectInputDTO);
        return ResponseEntity.ok(projectMapper.toResponse(updatedProject));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a project")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        deleteProject.execute(id);
        return ResponseEntity.noContent().build();
    }
}