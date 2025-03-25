package br.com.brunogodoif.projectmanagement.config;


import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.AuthRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.UserRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.TokenResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.persistence.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    private static final PostgreSQLContainer<?> postgreSQLContainer;

    static {
        postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17-alpine")).withDatabaseName(
                                                                                                            "projectmgmt-test").withUsername("postgres").withPassword("postgres")
                                                                                                    .withReuse(true);

        postgreSQLContainer.start();
    }

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
    }

    @Autowired(required = false)
    protected MockMvc mockMvc;

    @Autowired(required = false)
    protected ObjectMapper objectMapper;

    @Autowired(required = false)
    protected UserRepository userRepository;

    protected String createAndLoginAdminUser() throws Exception {
        checkMockMvcAndObjectMapper();

        final String adminUsername = "admin";
        final String adminPassword = "password";

        if (userRepository != null && userRepository.findByUsername(adminUsername).isEmpty()) {
            UserRequest adminRequest = new UserRequest(adminUsername,
                                                       adminPassword,
                                                       "admin@teste.com.br",
                                                       "Administrador do Sistema",
                                                       List.of("ADMIN", "USER")
                                                       // Atribuir papéis de ADMIN e USER
            );

            mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                                                      .content(objectMapper.writeValueAsString(adminRequest)))
                   .andExpect(status().isOk());
        }

        return loginUser(adminUsername, adminPassword);
    }

    protected String createAndLoginNormalUser() throws Exception {
        checkMockMvcAndObjectMapper();

        final String normalUsername = "user";
        final String normalPassword = "password";

        if (userRepository != null && userRepository.findByUsername(normalUsername).isEmpty()) {
            UserRequest userRequest = new UserRequest(normalUsername,
                                                      normalPassword,
                                                      "usuario@teste.com.br",
                                                      "Usuário Comum",
                                                      List.of("USER")
                                                      // Atribuir apenas o papel USER
            );

            mockMvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                                                      .content(objectMapper.writeValueAsString(userRequest)))
                   .andExpect(status().isOk());
        }

        // Fazer login com o usuário comum
        return loginUser(normalUsername, normalPassword);
    }

    protected String loginUser(String username, String password) throws Exception {
        checkMockMvcAndObjectMapper();

        AuthRequest authRequest = new AuthRequest(username, password);

        MvcResult authResult = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                                                                      .content(objectMapper.writeValueAsString(
                                                                              authRequest))).andExpect(status().isOk())
                                      .andReturn();

        TokenResponse tokenResponse = objectMapper.readValue(authResult.getResponse().getContentAsString(),
                                                             TokenResponse.class);

        return tokenResponse.token();
    }

    private void checkMockMvcAndObjectMapper() {
        if (mockMvc == null || objectMapper == null) {
            throw new IllegalStateException(
                    "MockMvc ou ObjectMapper não foram injetados. Verifique se a classe de teste está anotada com @AutoConfigureMockMvc.");
        }
    }
}