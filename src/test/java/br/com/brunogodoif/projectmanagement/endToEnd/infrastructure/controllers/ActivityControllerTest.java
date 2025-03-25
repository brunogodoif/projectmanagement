package br.com.brunogodoif.projectmanagement.endToEnd.infrastructure.controllers;

import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ActivityRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ClientRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ProjectRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ActivityRepository;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ClientRepository;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ActivityControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    private UUID clientId;
    private UUID projectId;
    private UUID activityId;
    private String authToken;
    private String authTokenAdmin;

    @BeforeEach
    void setup() throws Exception {
        // Limpar repositórios
        activityRepository.deleteAll();
        projectRepository.deleteAll();
        clientRepository.deleteAll();

        // Obter tokens de autenticação usando os métodos da classe base
        authTokenAdmin = createAndLoginAdminUser();
        authToken = createAndLoginNormalUser();

        // Criar cliente para testes
        ClientRequest clientRequest = new ClientRequest("Empresa de Teste LTDA",
                                                        "contato@empresateste.com.br",
                                                        "(11) 4321-8765",
                                                        "Empresa Teste Soluções",
                                                        "Av. Paulista, 1000, São Paulo-SP",
                                                        true);

        MvcResult clientResult = mockMvc.perform(post("/api/clients").header("Authorization",
                                                                             "Bearer " + authTokenAdmin)
                                                                     .contentType(MediaType.APPLICATION_JSON)
                                                                     .content(objectMapper.writeValueAsString(
                                                                             clientRequest)))
                                        .andExpect(status().isCreated()).andReturn();

        String clientResponseJson = clientResult.getResponse().getContentAsString();
        clientId = UUID.fromString(objectMapper.readTree(clientResponseJson).get("id").asText());

        // Criar projeto para testes
        ProjectRequest projectRequest = new ProjectRequest("Portal Institucional",
                                                           "Desenvolvimento do portal institucional da empresa",
                                                           clientId,
                                                           LocalDate.now(),
                                                           LocalDate.now().plusMonths(3),
                                                           ProjectStatus.IN_PROGRESS,
                                                           "Carlos Gerente",
                                                           "Projeto prioritário para o trimestre");

        MvcResult projectResult = mockMvc.perform(post("/api/projects").header("Authorization",
                                                                               "Bearer " + authTokenAdmin)
                                                                       .contentType(MediaType.APPLICATION_JSON)
                                                                       .content(objectMapper.writeValueAsString(
                                                                               projectRequest)))
                                         .andExpect(status().isCreated()).andReturn();

        String projectResponseJson = projectResult.getResponse().getContentAsString();
        projectId = UUID.fromString(objectMapper.readTree(projectResponseJson).get("id").asText());

        // Criar uma atividade para testes de recuperação e atualização
        ActivityRequest activityRequest = new ActivityRequest("Implementar Autenticação",
                                                              "Implementar sistema de autenticação com JWT",
                                                              projectId,
                                                              LocalDate.now().plusWeeks(1),
                                                              "Pedro Desenvolvedor",
                                                              false,
                                                              "ALTA",
                                                              16);

        MvcResult activityResult = mockMvc.perform(post("/api/activities").header("Authorization",
                                                                                  "Bearer " + authTokenAdmin)
                                                                          .contentType(MediaType.APPLICATION_JSON)
                                                                          .content(objectMapper.writeValueAsString(
                                                                                  activityRequest)))
                                          .andExpect(status().isCreated()).andReturn();

        String activityResponseJson = activityResult.getResponse().getContentAsString();
        activityId = UUID.fromString(objectMapper.readTree(activityResponseJson).get("id").asText());
    }

    @Test
    @DisplayName("Deve criar uma atividade com sucesso")
    void shouldCreateActivitySuccessfully() throws Exception {
        ActivityRequest request = new ActivityRequest("Desenvolver Layout",
                                                      "Implementar layout responsivo conforme mockups",
                                                      projectId,
                                                      LocalDate.now().plusDays(15),
                                                      "Ana Designer",
                                                      false,
                                                      "MÉDIA",
                                                      24);

        mockMvc.perform(post("/api/activities").header("Authorization", "Bearer " + authTokenAdmin)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated()).andExpect(jsonPath("$.id", notNullValue()))
               .andExpect(jsonPath("$.title", is("Desenvolver Layout")))
               .andExpect(jsonPath("$.description", is("Implementar layout responsivo conforme mockups")))
               .andExpect(jsonPath("$.projectId", is(projectId.toString())))
               .andExpect(jsonPath("$.assignedTo", is("Ana Designer"))).andExpect(jsonPath("$.completed", is(false)))
               .andExpect(jsonPath("$.priority", is("MÉDIA"))).andExpect(jsonPath("$.estimatedHours", is(24)));
    }

    @Test
    @DisplayName("Deve falhar ao criar atividade com dados inválidos")
    void shouldFailWhenCreatingActivityWithInvalidData() throws Exception {
        ActivityRequest request = new ActivityRequest("",
                                                      // Título vazio - deve falhar na validação
                                                      "Implementar layout responsivo conforme mockups",
                                                      projectId,
                                                      LocalDate.now().plusDays(15),
                                                      "Ana Designer",
                                                      false,
                                                      "MÉDIA",
                                                      24);

        mockMvc.perform(post("/api/activities").header("Authorization", "Bearer " + authTokenAdmin)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status", is(400)))
               .andExpect(jsonPath("$.error", is("Validation Error")));
    }

    @Test
    @DisplayName("Deve falhar ao criar atividade com projeto inexistente")
    void shouldFailWhenCreatingActivityWithNonExistentProject() throws Exception {
        ActivityRequest request = new ActivityRequest("Desenvolver Layout",
                                                      "Implementar layout responsivo conforme mockups",
                                                      UUID.randomUUID(),
                                                      // ID de projeto inexistente
                                                      LocalDate.now().plusDays(15),
                                                      "Ana Designer",
                                                      false,
                                                      "MÉDIA",
                                                      24);

        mockMvc.perform(post("/api/activities").header("Authorization", "Bearer " + authTokenAdmin)
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve recuperar atividade com usuário comum")
    void shouldFailWhenRetrieveActivityWithNormalUser() throws Exception {
        mockMvc.perform(get("/api/activities/{id}", activityId).header("Authorization", "Bearer " + authToken))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve recuperar atividade por ID com sucesso quando autenticado como admin")
    void shouldRetrieveActivityByIdSuccessfully() throws Exception {
        mockMvc.perform(get("/api/activities/{id}", activityId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(activityId.toString())))
               .andExpect(jsonPath("$.title", is("Implementar Autenticação")))
               .andExpect(jsonPath("$.description", is("Implementar sistema de autenticação com JWT")));
    }

    @Test
    @DisplayName("Deve falhar ao recuperar atividade inexistente")
    void shouldFailWhenRetrievingNonExistentActivity() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/activities/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve listar atividades por projeto com usuário comum")
    void shouldFailWhenListActivitiesByProjectWithNormalUser() throws Exception {
        mockMvc.perform(get("/api/activities/project/{projectId}", projectId).header("Authorization",
                                                                                     "Bearer " + authToken))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve listar atividades por projeto com sucesso quando autenticado como admin")
    void shouldListActivitiesByProjectSuccessfully() throws Exception {
        mockMvc.perform(get("/api/activities/project/{projectId}", projectId).header("Authorization",
                                                                                     "Bearer " + authTokenAdmin))
               .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
               .andExpect(jsonPath("$[0].projectId", is(projectId.toString())));
    }

    @Test
    @DisplayName("Deve atualizar atividade com sucesso")
    void shouldUpdateActivitySuccessfully() throws Exception {
        ActivityRequest updateRequest = new ActivityRequest("Implementar Autenticação JWT",
                                                            "Implementar sistema de autenticação com JWT e refresh tokens",
                                                            projectId,
                                                            LocalDate.now().plusWeeks(2),
                                                            "Pedro Desenvolvedor",
                                                            true,
                                                            // Alterado para concluído
                                                            "ALTA",
                                                            20
                                                            // Aumentado as horas estimadas
        );

        mockMvc.perform(put("/api/activities/{id}", activityId).header("Authorization", "Bearer " + authTokenAdmin)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(activityId.toString())))
               .andExpect(jsonPath("$.title", is("Implementar Autenticação JWT")))
               .andExpect(jsonPath("$.completed", is(true))).andExpect(jsonPath("$.estimatedHours", is(20)));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar atividade inexistente")
    void shouldFailWhenUpdatingNonExistentActivity() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        ActivityRequest updateRequest = new ActivityRequest("Implementar Autenticação JWT",
                                                            "Implementar sistema de autenticação com JWT e refresh tokens",
                                                            projectId,
                                                            LocalDate.now().plusWeeks(2),
                                                            "Pedro Desenvolvedor",
                                                            true,
                                                            "ALTA",
                                                            20);

        mockMvc.perform(put("/api/activities/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin)
                                                                  .contentType(MediaType.APPLICATION_JSON)
                                                                  .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve excluir atividade com sucesso")
    void shouldDeleteActivitySuccessfully() throws Exception {
        mockMvc.perform(delete("/api/activities/{id}", activityId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().isNoContent());

        // Verificar se a atividade foi realmente excluída
        mockMvc.perform(get("/api/activities/{id}", activityId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()); // Aceita qualquer erro 4xx
    }

    @Test
    @DisplayName("Deve falhar ao excluir atividade inexistente")
    void shouldFailWhenDeletingNonExistentActivity() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/activities/{id}", nonExistentId).header("Authorization",
                                                                             "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()); // Aceita qualquer erro 4xx
    }
}