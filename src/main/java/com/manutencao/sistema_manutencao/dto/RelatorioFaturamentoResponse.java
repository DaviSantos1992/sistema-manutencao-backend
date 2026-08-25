package com.manutencao.sistema_manutencao.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RelatorioFaturamentoResponse(
        LocalDate dataInicio,
        LocalDate dataFim,
        long totalOrdens,
        long totalConcluidas,
        BigDecimal faturamentoTotal
) {}