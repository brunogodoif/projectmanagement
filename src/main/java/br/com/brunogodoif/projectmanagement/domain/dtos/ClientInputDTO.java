package br.com.brunogodoif.projectmanagement.domain.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class ClientInputDTO {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String companyName;
    private String address;
    private boolean active;
}