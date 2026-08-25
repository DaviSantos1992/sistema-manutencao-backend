package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.ClienteRequest;
import com.manutencao.sistema_manutencao.dto.ClienteResponse;
import com.manutencao.sistema_manutencao.mapper.ClienteMapper;
import com.manutencao.sistema_manutencao.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteRestController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    // GET /api/clientes — lista todos
    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listarTodos()
                .stream().map(clienteMapper::toResponse).toList();
    }

    // GET /api/clientes/{id} — busca por id
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscar(@PathVariable Long id) {
        var cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok(clienteMapper.toResponse(cliente));
    }

    // POST /api/clientes — cria novo
    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody ClienteRequest request) {
        var salvo = clienteService.salvar(clienteMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clienteMapper.toResponse(salvo));
    }

    // PUT /api/clientes/{id} — atualiza
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ClienteRequest request) {
        var atualizado = clienteService.atualizar(id, clienteMapper.toEntity(request));
        return ResponseEntity.ok(clienteMapper.toResponse(atualizado));
    }

    // DELETE /api/clientes/{id} — exclui
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        clienteService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}