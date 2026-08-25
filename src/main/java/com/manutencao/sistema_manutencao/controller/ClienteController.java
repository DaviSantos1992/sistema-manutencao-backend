package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.ClienteRequest;
import com.manutencao.sistema_manutencao.dto.ClienteResponse;
import com.manutencao.sistema_manutencao.mapper.ClienteMapper;
import com.manutencao.sistema_manutencao.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    private final ClienteMapper clienteMapper;

    public ClienteController(ClienteService clienteService, ClienteMapper clienteMapper) {
        this.clienteService = clienteService;
        this.clienteMapper = clienteMapper;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.listarTodos()
                .stream().map(clienteMapper::toResponse).toList());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("cliente", new ClienteRequest(null, null, null));
        model.addAttribute("isEdicao", false);
        return "clientes/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("cliente") ClienteRequest request,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "clientes/form";
        }
        clienteService.salvar(clienteMapper.toEntity(request));
        redirect.addFlashAttribute("sucesso", "Cliente cadastrado com sucesso!");
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var cliente = clienteService.buscarPorId(id);
        model.addAttribute("cliente", new ClienteRequest(
                cliente.getNome(), cliente.getEmail(), cliente.getTelefone()));
        model.addAttribute("isEdicao", true);
        model.addAttribute("clienteId", id);
        return "clientes/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("cliente") ClienteRequest request,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("isEdicao", true);
            model.addAttribute("clienteId", id);
            return "clientes/form";
        }
        clienteService.atualizar(id, clienteMapper.toEntity(request));
        redirect.addFlashAttribute("sucesso", "Cliente atualizado com sucesso!");
        return "redirect:/clientes";
    }
}