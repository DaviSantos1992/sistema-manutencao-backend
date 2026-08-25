package com.manutencao.sistema_manutencao.service;

import com.manutencao.sistema_manutencao.entity.Servico;
import com.manutencao.sistema_manutencao.repository.ServicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Serviço não encontrado com id: " + id));
    }

    @Transactional
    public Servico salvar(Servico servico) {
        return servicoRepository.save(servico);
    }

    @Transactional
    public Servico atualizar(Long id, Servico dados) {
        Servico servico = buscarPorId(id);
        servico.setNome(dados.getNome());
        servico.setDescricao(dados.getDescricao());
        servico.setPrecoBase(dados.getPrecoBase());
        return servicoRepository.save(servico);
    }

    @Transactional
    public void excluir(Long id) {
        servicoRepository.deleteById(id);
    }
}