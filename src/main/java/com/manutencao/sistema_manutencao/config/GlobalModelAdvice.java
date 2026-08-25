package com.manutencao.sistema_manutencao.config;

import com.manutencao.sistema_manutencao.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UsuarioService usuarioService;

    public GlobalModelAdvice(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute("darkMode")
    public boolean darkMode() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            try {
                return usuarioService.buscarPorUsername(auth.getName()).isDarkMode();
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }
}