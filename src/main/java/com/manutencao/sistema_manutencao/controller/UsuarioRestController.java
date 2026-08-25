package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.UsuarioRequest;
import com.manutencao.sistema_manutencao.dto.UsuarioResponse;
import com.manutencao.sistema_manutencao.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioRestController {

    private final UsuarioService usuarioService;

    public UsuarioRestController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listarTodos().stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getUsername(), u.getRole()))
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id) {
        var u = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(new UsuarioResponse(u.getId(), u.getUsername(), u.getRole()));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@RequestBody UsuarioRequest request) {
        var salvo = usuarioService.salvar(request.username(), request.password(), request.role());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new UsuarioResponse(salvo.getId(), salvo.getUsername(), salvo.getRole()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                     @RequestBody UsuarioRequest request) {
        var atualizado = usuarioService.atualizar(id, request.username(), request.password(), request.role());
        return ResponseEntity.ok(new UsuarioResponse(atualizado.getId(), atualizado.getUsername(), atualizado.getRole()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        usuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}