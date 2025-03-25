package br.com.brunogodoif.projectmanagement.integration.application.usecases.project;

import br.com.brunogodoif.projectmanagement.application.usecases.client.CreateClientUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.CreateProjectUseCase;
import br.com.brunogodoif.projectmanagement.application.usecases.project.UpdateProjectUseCase;
import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.dtos.ProjectInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.entities.Project;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.domain.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UpdateProjectUseCaseTest extends BaseIntegrationTest {

    @Autowired
    private CreateProjectUseCase createProjectUseCase;

    @Autowired
    private UpdateProjectUseCase updateProjectUseCase;

    @Autowired
    private CreateClientUseCase createClientUseCase;

    private Project existingProject;
    private Client existingClient;
    private Client anotherClient;

    @BeforeEach
    void setUp() {
        // Criar primeiro cliente
        ClientInputDTO firstClientDTO = ClientInputDTO.builder()
                                                      .name("Cliente Primeiro")
                                                      .email("primeiro@teste.com")
                                                      .phone("11 1111-1111")
                                                      .companyName("Empresa Primeiro")
                                                      .address("Rua Primeiro, 100")
                                                      .active(true)
                                                      .build();

        existingClient = createClientUseCase.execute(firstClientDTO);

        // Criar segundo cliente
        ClientInputDTO secondClientDTO = ClientInputDTO.builder()
                                                       .name("Cliente Segundo")
                                                       .email("segundo@teste.com")
                                                       .phone("11 2222-2222")
                                                       .companyName("Empresa Segundo")
                                                       .address("Rua Segundo, 200")
                                                       .active(true)
                                                       .build();

        anotherClient = createClientUseCase.execute(secondClientDTO);

        // Criar projeto inicial
        ProjectInputDTO projectInputDTO = ProjectInputDTO.builder()
                                                         .name("Projeto Original")
                                                         .description("Descrição do Projeto Original")
                                                         .clientId(existingClient.getId())
                                                         .startDate(LocalDate.now())
                                                         .endDate(LocalDate.now().plusMonths(3))
                                                         .status(ProjectStatus.OPEN)
                                                         .manager("Gerente Original")
                                                         .notes("Notas do Projeto Original")
                                                         .build();

        existingProject = createProjectUseCase.execute(projectInputDTO);
    }

    @Test
    @DisplayName("Deve atualizar um projeto com sucesso")
    void shouldUpdateProjectSuccessfully() {
        // Arrange
        ProjectInputDTO updateDTO = ProjectInputDTO.builder()
                                                   .name("Projeto Atualizado")
                                                   .description("Descrição Atualizada")
                                                   .clientId(existingClient.getId())
                                                   .startDate(LocalDate.now().plusMonths(1))
                                                   .endDate(LocalDate.now().plusMonths(4))
                                                   .status(ProjectStatus.IN_PROGRESS)
                                                   .manager("Novo Gerente")
                                                   .notes("Novas Notas")
                                                   .build();

        // Act
        Project updatedProject = updateProjectUseCase.execute(existingProject.getId(), updateDTO);

        // Assert
        assertNotNull(updatedProject);
        assertEquals(existingProject.getId(), updatedProject.getId());
        assertEquals(updateDTO.getName(), updatedProject.getName());
        assertEquals(updateDTO.getDescription(), updatedProject.getDescription());
        assertEquals(updateDTO.getStartDate(), updatedProject.getStartDate());
        assertEquals(updateDTO.getEndDate(), updatedProject.getEndDate());
        assertEquals(updateDTO.getStatus(), updatedProject.getStatus());
        assertEquals(updateDTO.getManager(), updatedProject.getManager());
        assertEquals(updateDTO.getNotes(), updatedProject.getNotes());
    }

    @Test
    @DisplayName("Deve atualizar projeto com novo cliente")
    void shouldUpdateProjectWithNewClient() {
        // Arrange
        ProjectInputDTO updateDTO = ProjectInputDTO.builder()
                                                   .name("Projeto com Novo Cliente")
                                                   .description("Descrição Atualizada")
                                                   .clientId(anotherClient.getId())
                                                   .startDate(LocalDate.now().plusMonths(1))
                                                   .endDate(LocalDate.now().plusMonths(4))
                                                   .status(ProjectStatus.IN_PROGRESS)
                                                   .manager("Gerente Atualizado")
                                                   .notes("Notas Atualizadas")
                                                   .build();

        // Act
        Project updatedProject = updateProjectUseCase.execute(existingProject.getId(), updateDTO);

        // Assert
        assertNotNull(updatedProject);
        assertEquals(existingProject.getId(), updatedProject.getId());
        assertEquals(anotherClient.getId(), updatedProject.getClient().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar projeto inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentProject() {
        // Arrange
        ProjectInputDTO updateDTO = ProjectInputDTO.builder()
                                                   .name("Projeto Inexistente")
                                                   .description("Descrição do Projeto Inexistente")
                                                   .clientId(existingClient.getId())
                                                   .startDate(LocalDate.now())
                                                   .endDate(LocalDate.now().plusMonths(3))
                                                   .status(ProjectStatus.OPEN)
                                                   .manager("Gerente")
                                                   .notes("Notas")
                                                   .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            updateProjectUseCase.execute(UUID.randomUUID(), updateDTO);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar projeto com cliente inexistente")
    void shouldThrowExceptionWhenUpdatingProjectWithNonExistentClient() {
        // Arrange
        ProjectInputDTO updateDTO = ProjectInputDTO.builder()
                                                   .name("Projeto com Cliente Inexistente")
                                                   .description("Descrição do Projeto")
                                                   .clientId(UUID.randomUUID())
                                                   .startDate(LocalDate.now())
                                                   .endDate(LocalDate.now().plusMonths(3))
                                                   .status(ProjectStatus.OPEN)
                                                   .manager("Gerente")
                                                   .notes("Notas")
                                                   .build();

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            updateProjectUseCase.execute(existingProject.getId(), updateDTO);
        });
    }
}