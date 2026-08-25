package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.FaturamentoMensalResponse;
import com.manutencao.sistema_manutencao.dto.RelatorioFaturamentoResponse;
import com.manutencao.sistema_manutencao.service.OrdemServicoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioRestController {

    private final OrdemServicoService ordemServicoService;

    public RelatorioRestController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping("/faturamento")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> faturamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        RelatorioFaturamentoResponse relatorio =
                ordemServicoService.gerarRelatorioFaturamento(dataInicio, dataFim);
        List<FaturamentoMensalResponse> mensal =
                ordemServicoService.gerarFaturamentoMensal(dataInicio, dataFim);

        return ResponseEntity.ok(Map.of(
                "dataInicio", dataInicio.toString(),
                "dataFim", dataFim.toString(),
                "totalOrdens", relatorio.totalOrdens(),
                "totalConcluidas", relatorio.totalConcluidas(),
                "faturamentoTotal", relatorio.faturamentoTotal(),
                "faturamentoMensal", mensal
        ));
    }
}