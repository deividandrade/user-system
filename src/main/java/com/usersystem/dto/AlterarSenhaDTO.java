package com.usersystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlterarSenhaDTO {

    @NotBlank(message = "Senha atual é obrigatória")
    private String senhaAtual;

    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, message = "Nova senha deve ter no mínimo 6 caracteres")
    private String novaSenha;

    @NotBlank(message = "Confirmação da nova senha é obrigatória")
    private String confirmarNovaSenha;

    public boolean novasSenhasConferem() {
        return novaSenha != null && novaSenha.equals(confirmarNovaSenha);
    }
}
