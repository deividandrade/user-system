package com.usersystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CadastroDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String senha;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmarSenha;

    private String telefone;

    /**
     * Verifica se a senha e a confirmação são iguais.
     * Chamado na validação do controller.
     */
    public boolean senhasConferem() {
        return senha != null && senha.equals(confirmarSenha);
    }
}
