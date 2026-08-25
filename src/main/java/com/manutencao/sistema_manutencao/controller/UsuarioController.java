package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.UsuarioRequest;
import com.manutencao.sistema_manutencao.dto.UsuarioResponse;
import com.manutencao.sistema_manutencao.entity.Usuario;
import com.manutencao.sistema_manutencao.mapper.UsuarioMapper;
import com.manutencao.sistema_manutencao.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos()
                .stream().map(usuarioMapper::toResponse).toList());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new UsuarioRequest(null, null, "TECNICO"));
        model.addAttribute("isEdicao", false);
        return "usuarios/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("usuario") UsuarioRequest request,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "usuarios/form";
        }
        try {
            usuarioService.salvar(request.username(), request.password(), request.role());
            redirect.addFlashAttribute("sucesso", "Usuário criado com sucesso!");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/usuarios/novo";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", new UsuarioRequest(
                usuario.getUsername(), null, usuario.getRole()));
        model.addAttribute("isEdicao", true);
        model.addAttribute("usuarioId", id);
        return "usuarios/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("usuario") UsuarioRequest request,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("isEdicao", true);
            model.addAttribute("usuarioId", id);
            return "usuarios/form";
        }
        try {
            String usuarioLogado = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario alvo = usuarioService.buscarPorId(id);

            if (alvo.getUsername().equals(usuarioLogado)
                    && "ADMIN".equals(alvo.getRole())
                    && !"ADMIN".equals(request.role())) {
                redirect.addFlashAttribute("erro",
                        "Você não pode rebaixar o próprio perfil de ADMIN para TÉCNICO.");
                return "redirect:/usuarios/" + id + "/editar";
            }

            usuarioService.atualizar(id, request.username(), request.password(), request.role());
            redirect.addFlashAttribute("sucesso", "Usuário atualizado com sucesso!");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/usuarios/" + id + "/editar";
        }
        return "redirect:/usuarios";
    }
}