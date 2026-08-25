package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.service.UsuarioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PreferenciaController {

    private final UsuarioService usuarioService;

    public PreferenciaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/preferencias/dark-mode")
    public String alternarDarkMode(@RequestParam boolean darkMode,
                                   @RequestParam(defaultValue = "/") String destino,
                                   RedirectAttributes redirect) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.atualizarDarkMode(username, darkMode);
        return "redirect:" + destino;
    }
}