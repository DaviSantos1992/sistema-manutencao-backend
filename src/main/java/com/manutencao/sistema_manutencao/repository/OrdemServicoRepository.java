package com.manutencao.sistema_manutencao.repository;

import com.manutencao.sistema_manutencao.entity.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
    List<OrdemServico> findByClienteIdOrderByDataAberturaDesc(Long clienteId);
    List<OrdemServico> findAllByOrderByDataAberturaDesc();

    // Soma o valor total das ordens CONCLUÍDAS no período
    @Query("SELECT COALESCE(SUM(o.valorTotal), 0) FROM OrdemServico o " +
            "WHERE o.status = com.manutencao.sistema_manutencao.entity.OrdemServico.Status.CONCLUIDA " +
            "AND o.dataAbertura BETWEEN :inicio AND :fim")
    BigDecimal somarFaturamentoPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                          @Param("fim") LocalDateTime fim);

    // Conta ordens concluídas no período
    @Query("SELECT COUNT(o) FROM OrdemServico o " +
            "WHERE o.status = com.manutencao.sistema_manutencao.entity.OrdemServico.Status.CONCLUIDA " +
            "AND o.dataAbertura BETWEEN :inicio AND :fim")
    long contarConcluidasPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);

    // Conta todas as ordens no período
    @Query("SELECT COUNT(o) FROM OrdemServico o " +
            "WHERE o.dataAbertura BETWEEN :inicio AND :fim")
    long contarTotalPorPeriodo(@Param("inicio") LocalDateTime inicio,
                               @Param("fim") LocalDateTime fim);

    @Query("SELECT FUNCTION('DATE_FORMAT', o.dataAbertura, '%Y-%m') AS mes, " +
            "COALESCE(SUM(o.valorTotal), 0) " +
            "FROM OrdemServico o " +
            "WHERE o.status = com.manutencao.sistema_manutencao.entity.OrdemServico.Status.CONCLUIDA " +
            "AND o.dataAbertura BETWEEN :inicio AND :fim " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.dataAbertura, '%Y-%m') " +
            "ORDER BY mes")
    List<Object[]> somarFaturamentoMensal(@Param("inicio") LocalDateTime inicio,
                                          @Param("fim") LocalDateTime fim);
}