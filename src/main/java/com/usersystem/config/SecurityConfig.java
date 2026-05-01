package com.usersystem.config;

import com.usersystem.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
  
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager é necessário para autenticar programaticamente.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Regras de autorização por URL
            .authorizeHttpRequests(auth -> auth
                // URLs públicas (não precisam de login)
                .requestMatchers(
                    "/",
                    "/auth/login",
                    "/auth/cadastro",
                    "/auth/cadastro/processar",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/webjars/**"
                ).permitAll()
                // URLs exclusivas para ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Qualquer outra URL precisa de login
                .anyRequest().authenticated()
            )
            // Configuração do formulário de login
            .formLogin(form -> form
                .loginPage("/auth/login")               // URL da página de login
                .loginProcessingUrl("/auth/login")      // URL que processa o POST do formulário
                .usernameParameter("email")             // campo do formulário = email
                .passwordParameter("senha")             // campo do formulário = senha
                .defaultSuccessUrl("/dashboard", true)  // redireciona após login bem-sucedido
                .failureUrl("/auth/login?erro=true")    // redireciona se login falhar
                .permitAll()
            )
            // Configuração do logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"))
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)   // invalida a sessão
                .deleteCookies("JSESSIONID")   // remove o cookie de sessão
                .permitAll()
            )
            // Configura o provider de autenticação
            .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
