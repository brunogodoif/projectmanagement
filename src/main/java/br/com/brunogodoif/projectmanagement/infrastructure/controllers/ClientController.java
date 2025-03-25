package br.com.brunogodoif.projectmanagement.infrastructure.controllers;

import br.com.brunogodoif.projectmanagement.application.usecases.client.DeleteClientUseCase;
import br.com.brunogodoif.projectmanagement.domain.dtos.ClientInputDTO;
import br.com.brunogodoif.projectmanagement.domain.entities.Client;
import br.com.brunogodoif.projectmanagement.domain.usecases.DeleteEntityInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.CreateClientInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.GetClientInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.ListClientsInterface;
import br.com.brunogodoif.projectmanagement.domain.usecases.client.UpdateClientInterface;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.request.ClientRequest;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ClientDetailResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.controllers.response.ClientResponse;
import br.com.brunogodoif.projectmanagement.infrastructure.mappers.ClientMapper;
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
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Client management endpoints")
public class ClientController {

    private final CreateClientInterface createClientUseCase;
    private final GetClientInterface getClientUseCase;
    private final ListClientsInterface listClientsUseCase;
    private final UpdateClientInterface updateClientUseCase;
    private final DeleteEntityInterface<UUID> deleteClient;
    private final ClientMapper clientMapper;

    public ClientController(CreateClientInterface createClientUseCase, GetClientInterface getClientUseCase,
                            ListClientsInterface listClientsUseCase, UpdateClientInterface updateClientUseCase,
                            DeleteClientUseCase deleteClient, ClientMapper clientMapper
                           ) {
        this.createClientUseCase = createClientUseCase;
        this.getClientUseCase = getClientUseCase;
        this.listClientsUseCase = listClientsUseCase;
        this.updateClientUseCase = updateClientUseCase;
        this.deleteClient = deleteClient;
        this.clientMapper = clientMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new client")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {

        ClientInputDTO clientInputDTO = ClientInputDTO.builder()
                                                      .id(UUID.randomUUID())
                                                      .name(request.name())
                                                      .email(request.email())
                                                      .phone(request.phone())
                                                      .companyName(request.companyName())
                                                      .address(request.address())
                                                      .active(request.active())
                                                      .build();

        Client createdClient = createClientUseCase.execute(clientInputDTO);
        return new ResponseEntity<>(clientMapper.toResponse(createdClient), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get client by ID with associated projects")
    public ResponseEntity<ClientDetailResponse> getClient(@PathVariable UUID id) {
        Client client = getClientUseCase.execute(id);
        return ResponseEntity.ok(clientMapper.toDetailResponse(client));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "List all active clients")
    public ResponseEntity<List<ClientResponse>> listClients() {
        List<Client> clients = listClientsUseCase.execute();
        return ResponseEntity.ok(clientMapper.toResponseList(clients));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing client")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable UUID id,
                                                       @Valid @RequestBody ClientRequest request
                                                      ) {
        ClientInputDTO clientInputDTO = ClientInputDTO.builder()
                                                      .name(request.name())
                                                      .email(request.email())
                                                      .phone(request.phone())
                                                      .companyName(request.companyName())
                                                      .address(request.address())
                                                      .active(request.active())
                                                      .build();

        Client updatedClient = updateClientUseCase.execute(id, clientInputDTO);
        return ResponseEntity.ok(clientMapper.toResponse(updatedClient));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a client")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        deleteClient.execute(id);
        return ResponseEntity.noContent().build();
    }
}