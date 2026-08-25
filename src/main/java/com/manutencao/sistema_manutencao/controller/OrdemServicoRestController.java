package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.OrdemServicoRequest;
import com.manutencao.sistema_manutencao.dto.OrdemServicoResponse;
import com.manutencao.sistema_manutencao.dto.OrdemServicoStatusRequest;
import com.manutencao.sistema_manutencao.entity.OrdemServico;
import com.manutencao.sistema_manutencao.mapper.OrdemServicoMapper;
import com.manutencao.sistema_manutencao.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordens")
public class OrdemServicoRestController {

    private final OrdemServicoService ordemServicoService;
    private final OrdemServicoMapper ordemServicoMapper;

    public OrdemServicoRestController(OrdemServicoService ordemServicoService,
                                      OrdemServicoMapper ordemServicoMapper) {
        this.ordemServicoService = ordemServicoService;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    // GET /api/ordens — lista todas
    @GetMapping
    public List<OrdemServicoResponse> listar() {
        return ordemServicoService.listarTodas()
                .stream().map(ordemServicoMapper::toResponse).toList();
    }

    // GET /api/ordens/{id} — busca por id
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoMapper.toResponse(ordemServicoService.buscarPorId(id)));
    }

    // POST /api/ordens — cria nova OS
    @PostMapping
    public ResponseEntity<OrdemServicoResponse> criar(@Valid @RequestBody OrdemServicoRequest request) {
        var salva = ordemServicoService.criar(
                request.clienteId(),
                request.descricaoProblema(),
                request.servicoIds(),
                request.quantidades());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ordemServicoMapper.toResponse(salva));
    }

    // PUT /api/ordens/{id}/status — altera o status
    @PutMapping("/{id}/status")
    public ResponseEntity<OrdemServicoResponse> alterarStatus(@PathVariable Long id,
                                                              @RequestBody OrdemServicoStatusRequest request) {
        var atualizada = ordemServicoService.alterarStatus(id, request.status());
        return ResponseEntity.ok(ordemServicoMapper.toResponse(atualizada));
    }

    // DELETE /api/ordens/{id} — exclui
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        ordemServicoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}