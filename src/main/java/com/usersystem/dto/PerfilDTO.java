package com.usersystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PerfilDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @Size(max = 20, message = "Telefone pode ter no máximo 20 caracteres")
    private String telefone;

    @Size(max = 500, message = "Bio pode ter no máximo 500 caracteres")
    private String bio;
}