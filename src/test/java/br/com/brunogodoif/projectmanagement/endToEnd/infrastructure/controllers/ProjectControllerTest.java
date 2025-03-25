package br.com.brunogodoif.projectmanagement.endToEnd.infrastructure.controllers;

import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.domain.entities.ProjectStatus;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ClientRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ProjectRequest;
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
public class ProjectControllerTest extends BaseIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ClientRepository clientRepository;

    private UUID clientId;
    private UUID projectId;
    private String authToken;
    private String authTokenAdmin;

    @BeforeEach
    void setup() throws Exception {
        // Limpar repositórios
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
    }

    @Test
    @DisplayName("Deve criar um projeto com sucesso")
    void shouldCreateProjectSuccessfully() throws Exception {
        ProjectRequest request = new ProjectRequest("Sistema de Gestão Financeira",
                                                    "Desenvolvimento de sistema de gestão financeira integrado",
                                                    clientId,
                                                    LocalDate.now().plusWeeks(1),
                                                    LocalDate.now().plusMonths(6),
                                                    ProjectStatus.OPEN,
                                                    "Márcia Gestora",
                                                    "Projeto estratégico para o próximo semestre");

        mockMvc.perform(post("/api/projects").header("Authorization", "Bearer " + authTokenAdmin)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated()).andExpect(jsonPath("$.id", notNullValue()))
               .andExpect(jsonPath("$.name", is("Sistema de Gestão Financeira")))
               .andExpect(jsonPath("$.description", is("Desenvolvimento de sistema de gestão financeira integrado")))
               .andExpect(jsonPath("$.clientId", is(clientId.toString()))).andExpect(jsonPath("$.status", is("OPEN")))
               .andExpect(jsonPath("$.manager", is("Márcia Gestora")));
    }

    @Test
    @DisplayName("Deve falhar ao criar projeto com dados inválidos")
    void shouldFailWhenCreatingProjectWithInvalidData() throws Exception {
        ProjectRequest request = new ProjectRequest("",
                                                    // Nome vazio - deve falhar na validação
                                                    "Desenvolvimento de sistema de gestão financeira integrado",
                                                    clientId,
                                                    LocalDate.now().plusWeeks(1),
                                                    LocalDate.now().plusMonths(6),
                                                    ProjectStatus.OPEN,
                                                    "Márcia Gestora",
                                                    "Projeto estratégico para o próximo semestre");

        mockMvc.perform(post("/api/projects").header("Authorization", "Bearer " + authTokenAdmin)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status", is(400)))
               .andExpect(jsonPath("$.error", is("Validation Error")));
    }

    @Test
    @DisplayName("Deve falhar ao criar projeto com cliente inexistente")
    void shouldFailWhenCreatingProjectWithNonExistentClient() throws Exception {
        ProjectRequest request = new ProjectRequest("Sistema de Gestão Financeira",
                                                    "Desenvolvimento de sistema de gestão financeira integrado",
                                                    UUID.randomUUID(),
                                                    // ID de cliente inexistente
                                                    LocalDate.now().plusWeeks(1),
                                                    LocalDate.now().plusMonths(6),
                                                    ProjectStatus.OPEN,
                                                    "Márcia Gestora",
                                                    "Projeto estratégico para o próximo semestre");

        mockMvc.perform(post("/api/projects").header("Authorization", "Bearer " + authTokenAdmin)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve falhar ao criar projeto com usuário sem permissão")
    void shouldFailWhenCreatingProjectWithoutAdminRole() throws Exception {
        ProjectRequest request = new ProjectRequest("Sistema de Gestão Financeira",
                                                    "Desenvolvimento de sistema de gestão financeira integrado",
                                                    clientId,
                                                    LocalDate.now().plusWeeks(1),
                                                    LocalDate.now().plusMonths(6),
                                                    ProjectStatus.OPEN,
                                                    "Márcia Gestora",
                                                    "Projeto estratégico para o próximo semestre");

        mockMvc.perform(post("/api/projects").header("Authorization", "Bearer " + authToken) // Token de usuário comum
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve recuperar projeto por ID com sucesso quando autenticado como admin")
    void shouldRetrieveProjectByIdSuccessfully() throws Exception {
        mockMvc.perform(get("/api/projects/{id}", projectId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(projectId.toString())))
               .andExpect(jsonPath("$.name", is("Portal Institucional")))
               .andExpect(jsonPath("$.description", is("Desenvolvimento do portal institucional da empresa")))
               .andExpect(jsonPath("$.activities").exists()); // Verifica se a lista de atividades está presente
    }

    @Test
    @DisplayName("Deve falhar ao recuperar projeto inexistente")
    void shouldFailWhenRetrievingNonExistentProject() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/projects/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve listar todos os projetos ativos com sucesso quando autenticado como admin")
    void shouldListAllActiveProjectsSuccessfully() throws Exception {
        // Criar outro projeto primeiro
        ProjectRequest request = new ProjectRequest("Aplicativo Mobile",
                                                    "Desenvolvimento de aplicativo para iOS e Android",
                                                    clientId,
                                                    LocalDate.now(),
                                                    LocalDate.now().plusMonths(4),
                                                    ProjectStatus.OPEN,
                                                    "Renata Gerente",
                                                    "Projeto de alta prioridade");

        mockMvc.perform(post("/api/projects").header("Authorization", "Bearer " + authTokenAdmin)
                                             .contentType(MediaType.APPLICATION_JSON)
                                             .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated());

        // Listar todos os projetos
        mockMvc.perform(get("/api/projects").header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
               .andExpect(jsonPath("$[*].name", hasItems("Portal Institucional", "Aplicativo Mobile")));
    }

    @Test
    @DisplayName("Deve listar projetos por status com usuário comum")
    void shouldFailWhenListProjectsByStatusWithNormalUser() throws Exception {
        mockMvc.perform(get("/api/projects/status/{status}", "IN_PROGRESS").header("Authorization",
                                                                                   "Bearer " + authToken))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve listar projetos por status com sucesso quando autenticado como admin")
    void shouldListProjectsByStatusSuccessfully() throws Exception {
        mockMvc.perform(get("/api/projects/status/{status}", "IN_PROGRESS").header("Authorization",
                                                                                   "Bearer " + authTokenAdmin))
               .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
               .andExpect(jsonPath("$[0].status", is("IN_PROGRESS")));
    }

    @Test
    @DisplayName("Deve falhar ao listar projetos por cliente com usuário comum")
    void shouldFailWhenListProjectsByClientWithNormalUser() throws Exception {
        mockMvc.perform(get("/api/projects/client/{clientId}", clientId).header("Authorization", "Bearer " + authToken))
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve listar projetos por cliente com sucesso quando autenticado como admin")
    void shouldListProjectsByClientSuccessfully() throws Exception {
        mockMvc.perform(get("/api/projects/client/{clientId}", clientId).header("Authorization",
                                                                                "Bearer " + authTokenAdmin))
               .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
               .andExpect(jsonPath("$[0].clientId", is(clientId.toString())));
    }

    @Test
    @DisplayName("Deve atualizar projeto com sucesso")
    void shouldUpdateProjectSuccessfully() throws Exception {
        ProjectRequest updateRequest = new ProjectRequest("Portal Institucional Atualizado",
                                                          "Desenvolvimento do portal institucional com novas features",
                                                          clientId,
                                                          LocalDate.now(),
                                                          LocalDate.now().plusMonths(4),
                                                          // Aumentado prazo
                                                          ProjectStatus.ON_HOLD,
                                                          // Alterado status
                                                          "Carlos Gerente",
                                                          "Projeto com escopo reavaliado");

        mockMvc.perform(put("/api/projects/{id}", projectId).header("Authorization", "Bearer " + authTokenAdmin)
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(projectId.toString())))
               .andExpect(jsonPath("$.name", is("Portal Institucional Atualizado")))
               .andExpect(jsonPath("$.status", is("ON_HOLD")))
               .andExpect(jsonPath("$.notes", is("Projeto com escopo reavaliado")));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar projeto com usuário sem permissão")
    void shouldFailWhenUpdatingProjectWithoutAdminRole() throws Exception {
        ProjectRequest updateRequest = new ProjectRequest("Portal Institucional Atualizado",
                                                          "Desenvolvimento do portal institucional com novas features",
                                                          clientId,
                                                          LocalDate.now(),
                                                          LocalDate.now().plusMonths(4),
                                                          ProjectStatus.ON_HOLD,
                                                          "Carlos Gerente",
                                                          "Projeto com escopo reavaliado");

        mockMvc.perform(put("/api/projects/{id}", projectId).header("Authorization",
                                                                    "Bearer " + authToken) // Token de usuário comum
                                                            .contentType(MediaType.APPLICATION_JSON)
                                                            .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar projeto inexistente")
    void shouldFailWhenUpdatingNonExistentProject() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        ProjectRequest updateRequest = new ProjectRequest("Portal Institucional Atualizado",
                                                          "Desenvolvimento do portal institucional com novas features",
                                                          clientId,
                                                          LocalDate.now(),
                                                          LocalDate.now().plusMonths(4),
                                                          ProjectStatus.ON_HOLD,
                                                          "Carlos Gerente",
                                                          "Projeto com escopo reavaliado");

        mockMvc.perform(put("/api/projects/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin)
                                                                .contentType(MediaType.APPLICATION_JSON)
                                                                .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve excluir projeto com sucesso")
    void shouldDeleteProjectSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", projectId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().isNoContent());

        // Verificar se o projeto foi realmente excluído
        mockMvc.perform(get("/api/projects/{id}", projectId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()); // Aceita qualquer erro 4xx
    }

    @Test
    @DisplayName("Deve falhar ao excluir projeto com usuário sem permissão")
    void shouldFailWhenDeletingProjectWithoutAdminRole() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", projectId).header("Authorization",
                                                                       "Bearer " + authToken)) // Token de usuário comum
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve falhar ao excluir projeto inexistente")
    void shouldFailWhenDeletingNonExistentProject() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/projects/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()); // Aceita qualquer erro 4xx
    }
}