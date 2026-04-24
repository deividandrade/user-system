package com.usersystem.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Entidade que representa um Usuário no banco de dados.
 * 
 * @Entity     = mapeia esta classe para uma tabela no banco
 * @Table      = define o nome da tabela
 * @Data       = Lombok gera getters, setters, toString, equals, hashCode
 * @Builder    = Lombok permite construção fluente: User.builder().nome("João").build()
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // Chave primária com geração automática pelo banco de dados
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome completo - obrigatório, entre 2 e 100 caracteres
    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nome;

    // Email - obrigatório, único no banco, formato válido
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Senha - armazenada SEMPRE como hash (nunca em texto puro!)
    @NotBlank(message = "Senha é obrigatória")
    @Column(nullable = false)
    private String senha;

    // Telefone - opcional
    @Column(length = 20)
    private String telefone;

    // Bio/descrição do perfil - opcional
    @Column(columnDefinition = "TEXT")
    private String bio;

    // Papel/perfil do usuário (USER ou ADMIN)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_USER;

    // Se a conta está ativa ou não
    @Column(nullable = false)
    @Builder.Default
    private boolean ativo = true;

    // Data e hora do cadastro (preenchida automaticamente)
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    // Data da última atualização do perfil
    @Column
    private LocalDateTime dataAtualizacao;

    // Executado ANTES de salvar um novo registro no banco
    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
    }

    // Executado ANTES de atualizar um registro existente no banco
    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
