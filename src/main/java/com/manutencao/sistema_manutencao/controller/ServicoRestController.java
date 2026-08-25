package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.ServicoRequest;
import com.manutencao.sistema_manutencao.dto.ServicoResponse;
import com.manutencao.sistema_manutencao.mapper.ServicoMapper;
import com.manutencao.sistema_manutencao.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicos")
public class ServicoRestController {

    private final ServicoService servicoService;
    private final ServicoMapper servicoMapper;

    public ServicoRestController(ServicoService servicoService, ServicoMapper servicoMapper) {
        this.servicoService = servicoService;
        this.servicoMapper = servicoMapper;
    }

    @GetMapping
    public List<ServicoResponse> listar() {
        return servicoService.listarTodos()
                .stream().map(servicoMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(servicoMapper.toResponse(servicoService.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ServicoResponse> criar(@Valid @RequestBody ServicoRequest request) {
        var salvo = servicoService.salvar(servicoMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicoMapper.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ServicoRequest request) {
        var atualizado = servicoService.atualizar(id, servicoMapper.toEntity(request));
        return ResponseEntity.ok(servicoMapper.toResponse(atualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        servicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}