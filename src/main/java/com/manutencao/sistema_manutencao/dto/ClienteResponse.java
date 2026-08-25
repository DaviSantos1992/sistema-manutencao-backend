package com.manutencao.sistema_manutencao.dto;

public record ClienteResponse(
        Long id,
        String nome,
        String email,
        String telefone
) {}