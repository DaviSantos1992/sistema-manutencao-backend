package com.manutencao.sistema_manutencao.dto;

import java.math.BigDecimal;

public record ServicoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal precoBase
) {}