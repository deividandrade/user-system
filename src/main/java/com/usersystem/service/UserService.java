package com.usersystem.service;

import com.usersystem.dto.AlterarSenhaDTO;
import com.usersystem.dto.CadastroDTO;
import com.usersystem.dto.PerfilDTO;
import com.usersystem.model.Role;
import com.usersystem.model.User;
import com.usersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Camada de serviço com toda a lógica de negócio relacionada a usuários.
 * 
 * @Service     = Spring gerencia esta classe como um serviço
 * @Transactional = garante que operações de banco sejam atômicas
 * @Slf4j       = Lombok adiciona o logger
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // CADASTRO

    /**
     * Cadastra um novo usuário.
     * 
     * @param dto dados do formulário de cadastro
     * @return usuário salvo no banco
     * @throws IllegalArgumentException se email já existir ou senhas não conferem
     */
    @Transactional
    public User cadastrar(CadastroDTO dto) {
        log.debug("Tentando cadastrar usuário com email: {}", dto.getEmail());

        // Verifica se as senhas conferem
        if (!dto.senhasConferem()) {
            throw new IllegalArgumentException("As senhas não conferem");
        }

        // Verifica se o email já está cadastrado
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + dto.getEmail());
        }

        // Cria a entidade User a partir do DTO
        User novoUsuario = User.builder()
                .nome(dto.getNome())
                .email(dto.getEmail())
                // IMPORTANTE: a senha é criptografada aqui antes de salvar!
                .senha(passwordEncoder.encode(dto.getSenha()))
                .telefone(dto.getTelefone())
                .role(Role.ROLE_USER) // todo novo usuário começa como USER
                .ativo(true)
                .build();

        User salvo = userRepository.save(novoUsuario);
        log.info("Usuário cadastrado com sucesso: id={}, email={}", salvo.getId(), salvo.getEmail());
        return salvo;
    }

    // CONSULTAS

    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }

    public List<User> listarTodos() {
        return userRepository.findAll();
    }

    public List<User> listarAtivos() {
        return userRepository.findAllAtivosOrdenadosPorNome();
    }

    public List<User> buscarPorNome(String nome) {
        return userRepository.findByNomeContainingIgnoreCase(nome);
    }

    public long contarUsuariosAtivos() {
        return userRepository.countByAtivoTrue();
    }

    public long contarAdmins() {
        return userRepository.findByRole(Role.ROLE_ADMIN).size();
    }

    // ATUALIZAÇÃO DE PERFIL

    /**
     * Atualiza os dados do perfil de um usuário.
     */
    @Transactional
    public User atualizarPerfil(Long userId, PerfilDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        user.setNome(dto.getNome());
        user.setTelefone(dto.getTelefone());
        user.setBio(dto.getBio());

        User atualizado = userRepository.save(user);
        log.info("Perfil atualizado para userId={}", userId);
        return atualizado;
    }

    /**
     * Altera a senha de um usuário.
     * Verifica a senha atual antes de permitir a troca.
     */
    @Transactional
    public void alterarSenha(Long userId, AlterarSenhaDTO dto) {
        if (!dto.novasSenhasConferem()) {
            throw new IllegalArgumentException("As novas senhas não conferem");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        // Verifica se a senha atual está correta
        if (!passwordEncoder.matches(dto.getSenhaAtual(), user.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta");
        }

        // Criptografa e salva a nova senha
        user.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        userRepository.save(user);
        log.info("Senha alterada com sucesso para userId={}", userId);
    }

    // OPERAÇÕES do ADMIN

    /**
     * Ativa ou desativa a conta de um usuário.
     */
    @Transactional
    public void toggleStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        user.setAtivo(!user.isAtivo());
        userRepository.save(user);
        log.info("Status do userId={} alterado para ativo={}", userId, user.isAtivo());
    }

    /**
     * Altera o papel (role) de um usuário.
     */
    @Transactional
    public void alterarRole(Long userId, Role novoRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + userId));

        user.setRole(novoRole);
        userRepository.save(user);
        log.info("Role do userId={} alterado para {}", userId, novoRole);
    }

    /**
     * Remove um usuário do banco de dados.
     */
    @Transactional
    public void excluir(Long userId) {
        userRepository.deleteById(userId);
        log.info("Usuário excluído: userId={}", userId);
    }
}
