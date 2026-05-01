package com.usersystem.security;

import com.usersystem.model.User;
import com.usersystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

   
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário no banco de dados pelo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado com o email: " + email));

        // Verifica se a conta está ativa
        if (!user.isAtivo()) {
            throw new UsernameNotFoundException("Conta desativada: " + email);
        }

        // Converte o Role do nosso sistema para o formato que o Spring Security entende
       
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        // Retorna o objeto UserDetails que o Spring Security vai usar
        // org.springframework.security.core.userdetails.User (não confundir com a entidade!!)
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),   // username = email
                user.getSenha(),   // senha já está em hash no banco
                authorities        // permissões/roles
        );
    }
}
