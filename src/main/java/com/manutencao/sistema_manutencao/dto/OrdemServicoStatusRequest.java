package com.manutencao.sistema_manutencao.dto;

import com.manutencao.sistema_manutencao.entity.OrdemServico;
import jakarta.validation.constraints.NotNull;

public record OrdemServicoStatusRequest(
        @NotNull OrdemServico.Status status
) {}