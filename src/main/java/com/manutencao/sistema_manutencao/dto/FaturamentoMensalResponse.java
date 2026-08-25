package com.manutencao.sistema_manutencao.dto;

import java.math.BigDecimal;

public record FaturamentoMensalResponse(
        String mes,      // ex.: "2026-08"
        BigDecimal valor
) {}