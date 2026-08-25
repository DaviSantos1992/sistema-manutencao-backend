package com.manutencao.sistema_manutencao.dto;

import com.manutencao.sistema_manutencao.entity.OrdemServico.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdemServicoResponse(
        Long id,
        String clienteNome,
        LocalDateTime dataAbertura,
        LocalDateTime dataConclusao,
        String descricaoProblema,
        Status status,
        BigDecimal valorTotal
) {}