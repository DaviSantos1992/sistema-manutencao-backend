package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.FaturamentoMensalResponse;
import com.manutencao.sistema_manutencao.dto.RelatorioFaturamentoResponse;
import com.manutencao.sistema_manutencao.service.OrdemServicoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;

// Apache POI (Excel)
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// OpenPDF (PDF)
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
//import com.lowagie.text.BaseColor;
import java.awt.Color;

// HTTP / IO
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private final OrdemServicoService ordemServicoService;

    public RelatorioController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @GetMapping("/faturamento")
    @PreAuthorize("hasRole('ADMIN')")
    public String faturamento(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) boolean limpo,
            Model model) {

        // Só calcula se as DUAS datas foram informadas
        boolean temFiltro = dataInicio != null && dataFim != null;

        // Estado limpo: sem datas, sem resultado (início da tela OU clique em Limpar)
        if (limpo || !temFiltro) {
            model.addAttribute("limpo", true);
            model.addAttribute("dataInicio", null);
            model.addAttribute("dataFim", null);
            model.addAttribute("relatorio",
                    new RelatorioFaturamentoResponse(null, null, 0, 0, BigDecimal.ZERO));
            model.addAttribute("faturamentoMensal", List.of());
            return "relatorios/faturamento";
        }

        RelatorioFaturamentoResponse relatorio =
                ordemServicoService.gerarRelatorioFaturamento(dataInicio, dataFim);
        model.addAttribute("limpo", false);
        model.addAttribute("relatorio", relatorio);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("faturamentoMensal",
                ordemServicoService.gerarFaturamentoMensal(dataInicio, dataFim));
        return "relatorios/faturamento";
    }

    // ==================== EXPORTAÇÃO COM SELETOR DE TIPO ====================

    @GetMapping("/faturamento/exportar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarFaturamento(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) throws Exception {

        if (dataInicio == null) dataInicio = LocalDate.now().withDayOfMonth(1);
        if (dataFim == null) dataFim = LocalDate.now();
        if (tipo == null) tipo = "excel";

        RelatorioFaturamentoResponse relatorio =
                ordemServicoService.gerarRelatorioFaturamento(dataInicio, dataFim);
        List<FaturamentoMensalResponse> mensal =
                ordemServicoService.gerarFaturamentoMensal(dataInicio, dataFim);

        return switch (tipo.toLowerCase()) {
            case "csv" -> exportarCsv(relatorio, mensal, dataInicio, dataFim);
            case "pdf" -> exportarPdf(relatorio, mensal, dataInicio, dataFim);
            default -> exportarExcel(relatorio, mensal, dataInicio, dataFim);
        };
    }

    // ---------- CSV (sem dependência extra) ----------
    private ResponseEntity<byte[]> exportarCsv(RelatorioFaturamentoResponse relatorio,
                                               List<FaturamentoMensalResponse> mensal,
                                               LocalDate dataInicio, LocalDate dataFim) {
        StringBuilder csv = new StringBuilder();
        csv.append("Relatório de Faturamento\n");
        csv.append("Período,").append(dataInicio).append(" a ").append(dataFim).append("\n\n");
        csv.append("Total de Ordens,").append(relatorio.totalOrdens()).append("\n");
        csv.append("Ordens Concluídas,").append(relatorio.totalConcluidas()).append("\n");
        csv.append("Faturamento Total,").append(relatorio.faturamentoTotal()).append("\n\n");
        csv.append("Mês,Valor (R$)\n");
        for (FaturamentoMensalResponse fm : mensal) {
            csv.append(fm.mes()).append(",").append(fm.valor()).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment",
                "faturamento_" + dataInicio + "_a_" + dataFim + ".csv");
        return ResponseEntity.ok().headers(headers)
                .body(csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    // ---------- PDF (OpenPDF) ----------
    private ResponseEntity<byte[]> exportarPdf(RelatorioFaturamentoResponse relatorio,
                                               List<FaturamentoMensalResponse> mensal,
                                               LocalDate dataInicio, LocalDate dataFim) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        //Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLUE);
        //Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Font.NORMAL, Color.BLUE);
        // Azul padrão do sistema (#0d6efd)
        Color azulSistema = new Color(13, 110, 253);
        Font titulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, azulSistema);
        Font negrito = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph("Relatório de Faturamento", titulo));
        document.add(new Paragraph("Período: " + dataInicio + " a " + dataFim, normal));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Total de Ordens: " + relatorio.totalOrdens(), negrito));
        document.add(new Paragraph("Ordens Concluídas: " + relatorio.totalConcluidas(), negrito));
        document.add(new Paragraph("Faturamento Total: R$ " + relatorio.faturamentoTotal(), negrito));
        document.add(new Paragraph(" "));

        document.add(new Paragraph("Faturamento Mensal", titulo));
        PdfPTable tabela = new PdfPTable(2);
        tabela.addCell("Mês");
        tabela.addCell("Valor (R$)");
        for (FaturamentoMensalResponse fm : mensal) {
            tabela.addCell(fm.mes());
            tabela.addCell(fm.valor().toString());
        }
        document.add(tabela);

        document.close();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment",
                "faturamento_" + dataInicio + "_a_" + dataFim + ".pdf");
        return ResponseEntity.ok().headers(headers).body(out.toByteArray());
    }

    // ---------- Excel (Apache POI — código que você já tinha, extraído) ----------
    private ResponseEntity<byte[]> exportarExcel(RelatorioFaturamentoResponse relatorio,
                                                 List<FaturamentoMensalResponse> mensal,
                                                 LocalDate dataInicio, LocalDate dataFim) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Aba Resumo
            Sheet resumo = workbook.createSheet("Resumo");
            Row r1 = resumo.createRow(0);
            r1.createCell(0).setCellValue("Relatório de Faturamento");
            Row r2 = resumo.createRow(1);
            r2.createCell(0).setCellValue("Período: " + dataInicio + " a " + dataFim);
            Row r3 = resumo.createRow(3);
            r3.createCell(0).setCellValue("Total de Ordens");
            r3.createCell(1).setCellValue(relatorio.totalOrdens());
            Row r4 = resumo.createRow(4);
            r4.createCell(0).setCellValue("Ordens Concluídas");
            r4.createCell(1).setCellValue(relatorio.totalConcluidas());
            Row r5 = resumo.createRow(5);
            r5.createCell(0).setCellValue("Faturamento Total");
            r5.createCell(1).setCellValue(relatorio.faturamentoTotal().doubleValue());
            resumo.autoSizeColumn(0);

            // Aba Mensal
            Sheet mensalSheet = workbook.createSheet("Faturamento Mensal");
            Row header = mensalSheet.createRow(0);
            header.createCell(0).setCellValue("Mês");
            header.createCell(1).setCellValue("Valor (R$)");
            int linha = 1;
            for (FaturamentoMensalResponse fm : mensal) {
                Row row = mensalSheet.createRow(linha++);
                row.createCell(0).setCellValue(fm.mes());
                row.createCell(1).setCellValue(fm.valor().doubleValue());
            }
            mensalSheet.autoSizeColumn(0);
            mensalSheet.autoSizeColumn(1);

            workbook.write(out);
            byte[] bytes = out.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment",
                    "faturamento_" + dataInicio + "_a_" + dataFim + ".xlsx");

            return ResponseEntity.ok().headers(headers).body(bytes);
        }
    }
}