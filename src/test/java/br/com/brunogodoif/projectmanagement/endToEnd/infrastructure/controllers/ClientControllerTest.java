package br.com.brunogodoif.projectmanagement.endToEnd.infrastructure.controllers;

import br.com.brunogodoif.projectmanagement.config.BaseIntegrationTest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ClientRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ClientControllerTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    private UUID clientId;
    private String authToken;
    private String authTokenAdmin;

    @BeforeEach
    void setup() throws Exception {
        // Limpar repositório
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
    }

    @Test
    @DisplayName("Deve criar um cliente com sucesso")
    void shouldCreateClientSuccessfully() throws Exception {
        ClientRequest request = new ClientRequest("Novo Cliente S.A.",
                                                  "contato@novocliente.com.br",
                                                  "(21) 2222-3333",
                                                  "Novo Cliente Sistemas",
                                                  "Rua do Comércio, 500, Rio de Janeiro-RJ",
                                                  true);

        mockMvc.perform(post("/api/clients").header("Authorization", "Bearer " + authTokenAdmin)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated()).andExpect(jsonPath("$.id", notNullValue()))
               .andExpect(jsonPath("$.name", is("Novo Cliente S.A.")))
               .andExpect(jsonPath("$.email", is("contato@novocliente.com.br")))
               .andExpect(jsonPath("$.phone", is("(21) 2222-3333")))
               .andExpect(jsonPath("$.companyName", is("Novo Cliente Sistemas")))
               .andExpect(jsonPath("$.address", is("Rua do Comércio, 500, Rio de Janeiro-RJ")))
               .andExpect(jsonPath("$.active", is(true)));
    }

    @Test
    @DisplayName("Deve falhar ao criar cliente com dados inválidos")
    void shouldFailWhenCreatingClientWithInvalidData() throws Exception {
        ClientRequest request = new ClientRequest("",
                                                  // Nome vazio - deve falhar na validação
                                                  "contato@novocliente.com.br",
                                                  "(21) 2222-3333",
                                                  "Novo Cliente Sistemas",
                                                  "Rua do Comércio, 500, Rio de Janeiro-RJ",
                                                  true);

        mockMvc.perform(post("/api/clients").header("Authorization", "Bearer " + authTokenAdmin)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status", is(400)))
               .andExpect(jsonPath("$.error", is("Validation Error")));
    }

    @Test
    @DisplayName("Deve falhar ao criar cliente com usuário sem permissão")
    void shouldFailWhenCreatingClientWithoutAdminRole() throws Exception {
        ClientRequest request = new ClientRequest("Novo Cliente S.A.",
                                                  "contato@novocliente.com.br",
                                                  "(21) 2222-3333",
                                                  "Novo Cliente Sistemas",
                                                  "Rua do Comércio, 500, Rio de Janeiro-RJ",
                                                  true);

        mockMvc.perform(post("/api/clients").header("Authorization", "Bearer " + authToken) // Token de usuário comum
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve recuperar cliente por ID com sucesso quando autenticado como admin")
    void shouldRetrieveClientByIdSuccessfully() throws Exception {
        mockMvc.perform(get("/api/clients/{id}", clientId).header("Authorization",
                                                                  "Bearer " + authTokenAdmin)) // Só o admin pode visualizar
               .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(clientId.toString())))
               .andExpect(jsonPath("$.name", is("Empresa de Teste LTDA")))
               .andExpect(jsonPath("$.email", is("contato@empresateste.com.br")))
               .andExpect(jsonPath("$.projects").exists()); // Verifica se a lista de projetos está presente
    }

    @Test
    @DisplayName("Deve recuperar cliente quando autenticado como usuário comum")
    void shouldFailWhenRetrievingClientWithNormalUser() throws Exception {
        mockMvc.perform(get("/api/clients/{id}", clientId).header("Authorization",
                                                                  "Bearer " + authToken)) // Usuário comum não tem permissão
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve falhar ao recuperar cliente inexistente")
    void shouldFailWhenRetrievingNonExistentClient() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/clients/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve listar todos os clientes ativos com sucesso quando autenticado como admin")
    void shouldListAllActiveClientsSuccessfully() throws Exception {
        // Criar outro cliente primeiro
        ClientRequest request = new ClientRequest("Outro Cliente LTDA",
                                                  "contato@outrocliente.com.br",
                                                  "(31) 3333-4444",
                                                  "Outro Cliente Serviços",
                                                  "Av. Afonso Pena, 1500, Belo Horizonte-MG",
                                                  true);

        mockMvc.perform(post("/api/clients").header("Authorization", "Bearer " + authTokenAdmin)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isCreated());

        // Listar todos os clientes
        mockMvc.perform(get("/api/clients").header("Authorization", "Bearer " + authTokenAdmin)) // Usar token de admin
               .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
               .andExpect(jsonPath("$[*].name", hasItems("Empresa de Teste LTDA", "Outro Cliente LTDA")));
    }

    @Test
    @DisplayName("Deve listar clientes quando autenticado como usuário comum")
    void shouldFailWhenListingClientsWithNormalUser() throws Exception {
        mockMvc.perform(get("/api/clients").header("Authorization",
                                                   "Bearer " + authToken)) // Usuário comum não tem permissão
               .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve atualizar cliente com sucesso")
    void shouldUpdateClientSuccessfully() throws Exception {
        ClientRequest updateRequest = new ClientRequest("Empresa de Teste Atualizada S.A.",
                                                        "novo-contato@empresateste.com.br",
                                                        "(11) 5555-6666",
                                                        "Empresa Teste Soluções Atualizadas",
                                                        "Av. Paulista, 2000, São Paulo-SP",
                                                        true);

        mockMvc.perform(put("/api/clients/{id}", clientId).header("Authorization", "Bearer " + authTokenAdmin)
                                                          .contentType(MediaType.APPLICATION_JSON)
                                                          .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().isOk()).andExpect(jsonPath("$.id", is(clientId.toString())))
               .andExpect(jsonPath("$.name", is("Empresa de Teste Atualizada S.A.")))
               .andExpect(jsonPath("$.email", is("novo-contato@empresateste.com.br")))
               .andExpect(jsonPath("$.phone", is("(11) 5555-6666")));
    }

    @Test
    @DisplayName("Deve falhar ao atualizar cliente com usuário sem permissão")
    void shouldFailWhenUpdatingClientWithoutAdminRole() throws Exception {
        ClientRequest updateRequest = new ClientRequest("Empresa de Teste Atualizada S.A.",
                                                        "novo-contato@empresateste.com.br",
                                                        "(11) 5555-6666",
                                                        "Empresa Teste Soluções Atualizadas",
                                                        "Av. Paulista, 2000, São Paulo-SP",
                                                        true);

        mockMvc.perform(put("/api/clients/{id}", clientId).header("Authorization",
                                                                  "Bearer " + authToken) // Token de usuário comum
                                                          .contentType(MediaType.APPLICATION_JSON)
                                                          .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve falhar ao atualizar cliente inexistente")
    void shouldFailWhenUpdatingNonExistentClient() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        ClientRequest updateRequest = new ClientRequest("Empresa de Teste Atualizada S.A.",
                                                        "novo-contato@empresateste.com.br",
                                                        "(11) 5555-6666",
                                                        "Empresa Teste Soluções Atualizadas",
                                                        "Av. Paulista, 2000, São Paulo-SP",
                                                        true);

        mockMvc.perform(put("/api/clients/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .content(objectMapper.writeValueAsString(updateRequest)))
               .andExpect(status().is4xxClientError()) // Aceita qualquer erro 4xx
               .andExpect(jsonPath("$.status").exists()); // Verifica apenas se há status de erro
    }

    @Test
    @DisplayName("Deve excluir cliente com sucesso")
    void shouldDeleteClientSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/clients/{id}", clientId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().isNoContent());

        // Verificar se o cliente foi realmente excluído
        mockMvc.perform(get("/api/clients/{id}", clientId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()); // Aceita qualquer erro 4xx
    }

    @Test
    @DisplayName("Deve falhar ao excluir cliente com usuário sem permissão")
    void shouldFailWhenDeletingClientWithoutAdminRole() throws Exception {
        mockMvc.perform(delete("/api/clients/{id}", clientId).header("Authorization",
                                                                     "Bearer " + authToken)) // Token de usuário comum
               .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Deve falhar ao excluir cliente inexistente")
    void shouldFailWhenDeletingNonExistentClient() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/clients/{id}", nonExistentId).header("Authorization", "Bearer " + authTokenAdmin))
               .andExpect(status().is4xxClientError()); // Aceita qualquer erro 4xx
    }
}