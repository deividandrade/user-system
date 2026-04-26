package com.usersystem.repository;

import com.usersystem.model.Role;
import com.usersystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	// Busca usuário pelo email (usado no login)
	Optional<User> findByEmail(String email);

	// Verifica se já existe um usuário com este email (usado no cadastro)
	boolean existsByEmail(String email);

	// Lista todos os usuários com um determinado papel
	List<User> findByRole(Role role);

	// Busca usuários pelo nome (busca parcial, ignorando maiúsculas/minúsculas)
	List<User> findByNomeContainingIgnoreCase(String nome);

	// Conta quantos usuários estão ativos
	long countByAtivoTrue();

	// Query JPQL personalizada - busca usuários ativos ordenados por nome
	@Query("SELECT u FROM User u WHERE u.ativo = true ORDER BY u.nome ASC")
	List<User> findAllAtivosOrdenadosPorNome();
}
