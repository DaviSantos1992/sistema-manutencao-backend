package com.manutencao.sistema_manutencao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ServicoRequest(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        String descricao,

        @NotNull(message = "Preço base é obrigatório")
        @PositiveOrZero(message = "Preço não pode ser negativo")
        BigDecimal precoBase
) {}