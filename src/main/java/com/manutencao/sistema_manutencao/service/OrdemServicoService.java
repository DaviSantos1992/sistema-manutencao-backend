package com.manutencao.sistema_manutencao.service;

import com.manutencao.sistema_manutencao.dto.FaturamentoMensalResponse;
import com.manutencao.sistema_manutencao.dto.RelatorioFaturamentoResponse;
import com.manutencao.sistema_manutencao.entity.Cliente;
import com.manutencao.sistema_manutencao.entity.ItemOrdemServico;
import com.manutencao.sistema_manutencao.entity.OrdemServico;
import com.manutencao.sistema_manutencao.entity.Servico;
import com.manutencao.sistema_manutencao.repository.OrdemServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ClienteService clienteService;
    private final ServicoService servicoService;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               ClienteService clienteService,
                               ServicoService servicoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.clienteService = clienteService;
        this.servicoService = servicoService;
    }

    @Transactional(readOnly = true)
    public List<OrdemServico> listarTodas() {
        return ordemServicoRepository.findAllByOrderByDataAberturaDesc();
    }

    @Transactional(readOnly = true)
    public OrdemServico buscarPorId(Long id) {
        return ordemServicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de serviço não encontrada com id: " + id));
    }

    @Transactional
    public OrdemServico criar(Long clienteId, String descricaoProblema,
                              List<Long> servicoIds, List<Integer> quantidades) {
        Cliente cliente = clienteService.buscarPorId(clienteId);
        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setDescricaoProblema(descricaoProblema);
        os.setStatus(OrdemServico.Status.ABERTA);

        // Adiciona cada serviço à OS
        for (int i = 0; i < servicoIds.size(); i++) {
            Servico servico = servicoService.buscarPorId(servicoIds.get(i));
            int qtd = quantidades.get(i);
            os.adicionarItem(servico, qtd);
        }

        return ordemServicoRepository.save(os);
    }

    @Transactional
    public OrdemServico alterarStatus(Long id, OrdemServico.Status novoStatus) {
        OrdemServico os = buscarPorId(id);

        // Regra de negócio: só conclui se tiver pelo menos um item
        if (novoStatus == OrdemServico.Status.CONCLUIDA && os.getItens().isEmpty()) {
            throw new RuntimeException("Não é possível concluir uma OS sem itens de serviço.");
        }

        os.setStatus(novoStatus);
        if (novoStatus == OrdemServico.Status.CONCLUIDA) {
            os.setDataConclusao(LocalDateTime.now());
        }
        return ordemServicoRepository.save(os);
    }

    @Transactional(readOnly = true)
    public RelatorioFaturamentoResponse gerarRelatorioFaturamento(LocalDate dataInicio, LocalDate dataFim) {
        // Fim do dia para incluir o último dia completo
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        BigDecimal total = ordemServicoRepository.somarFaturamentoPorPeriodo(inicio, fim);
        long concluidas = ordemServicoRepository.contarConcluidasPorPeriodo(inicio, fim);
        long totalOrdens = ordemServicoRepository.contarTotalPorPeriodo(inicio, fim);

        return new RelatorioFaturamentoResponse(dataInicio, dataFim, totalOrdens, concluidas, total);
    }

    @Transactional(readOnly = true)
    public List<FaturamentoMensalResponse> gerarFaturamentoMensal(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59);

        return ordemServicoRepository.somarFaturamentoMensal(inicio, fim)
                .stream()
                .map(linha -> new FaturamentoMensalResponse(
                        (String) linha[0], (BigDecimal) linha[1]))
                .toList();
    }

    @Transactional
    public void excluir(Long id) {
        ordemServicoRepository.deleteById(id);
    }
}