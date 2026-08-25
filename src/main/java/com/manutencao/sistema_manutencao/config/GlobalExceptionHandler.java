package com.manutencao.sistema_manutencao.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 404 - página ou recurso não encontrado
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNaoEncontrado(NoResourceFoundException ex, Model model) {
        model.addAttribute("codigo", 404);
        model.addAttribute("titulo", "Página não encontrada");
        model.addAttribute("mensagem", "O recurso solicitado não existe ou foi removido.");
        return "erro";
    }

    // 403 - acesso negado (segurança)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAcessoNegado(AccessDeniedException ex, Model model) {
        model.addAttribute("codigo", 403);
        model.addAttribute("titulo", "Acesso negado");
        model.addAttribute("mensagem", "Você não tem permissão para acessar esta página.");
        return "erro";
    }

    // 500 - erro de execução (inclui erros de template, banco, etc.)
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleErroRuntime(RuntimeException ex, Model model) {
        log.error("Erro inesperado no sistema", ex);
        model.addAttribute("codigo", 500);
        model.addAttribute("titulo", "Erro interno");
        model.addAttribute("mensagem",
                ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro inesperado.");
        return "erro";
    }

    // 500 - qualquer outra exceção não prevista
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleErroGenerico(Exception ex, Model model) {
        log.error("Erro inesperado no sistema", ex);
        model.addAttribute("codigo", 500);
        model.addAttribute("titulo", "Erro interno");
        model.addAttribute("mensagem", "Ocorreu um erro inesperado. Tente novamente mais tarde.");
        return "erro";
    }
}