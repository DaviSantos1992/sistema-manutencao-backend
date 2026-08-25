package com.manutencao.sistema_manutencao.repository;

import com.manutencao.sistema_manutencao.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // Busca clientes por nome (ignorando maiúsculas/minúsculas)
    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    // Verifica se um e-mail já existe (para validação)
    boolean existsByEmail(String email);

    // Busca por e-mail
    Optional<Cliente> findByEmail(String email);
}