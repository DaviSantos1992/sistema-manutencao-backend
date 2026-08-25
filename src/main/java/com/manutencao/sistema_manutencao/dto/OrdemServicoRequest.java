package com.manutencao.sistema_manutencao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record OrdemServicoRequest(
        @NotNull Long clienteId,
        @NotBlank String descricaoProblema,
        @NotEmpty List<Long> servicoIds,
        @NotEmpty List<Integer> quantidades
) {}