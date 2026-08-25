package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.ServicoRequest;
import com.manutencao.sistema_manutencao.dto.ServicoResponse;
import com.manutencao.sistema_manutencao.mapper.ServicoMapper;
import com.manutencao.sistema_manutencao.service.ServicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;
    private final ServicoMapper servicoMapper;

    public ServicoController(ServicoService servicoService, ServicoMapper servicoMapper) {
        this.servicoService = servicoService;
        this.servicoMapper = servicoMapper;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("servicos", servicoService.listarTodos()
                .stream().map(servicoMapper::toResponse).toList());
        return "servicos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("servico", new ServicoRequest(null, null, null));
        model.addAttribute("isEdicao", false);
        return "servicos/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("servico") ServicoRequest request,
                         BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "servicos/form";
        }
        servicoService.salvar(servicoMapper.toEntity(request));
        redirect.addFlashAttribute("sucesso", "Serviço cadastrado com sucesso!");
        return "redirect:/servicos";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        var servico = servicoService.buscarPorId(id);
        model.addAttribute("servico", new ServicoRequest(
                servico.getNome(), servico.getDescricao(), servico.getPrecoBase()));
        model.addAttribute("isEdicao", true);
        model.addAttribute("servicoId", id);
        return "servicos/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id,
                            @Valid @ModelAttribute("servico") ServicoRequest request,
                            BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("isEdicao", true);
            model.addAttribute("servicoId", id);
            return "servicos/form";
        }
        servicoService.atualizar(id, servicoMapper.toEntity(request));
        redirect.addFlashAttribute("sucesso", "Serviço atualizado com sucesso!");
        return "redirect:/servicos";
    }
}