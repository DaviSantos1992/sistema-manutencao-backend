package com.manutencao.sistema_manutencao.controller;

import com.manutencao.sistema_manutencao.dto.OrdemServicoRequest;
import com.manutencao.sistema_manutencao.dto.OrdemServicoResponse;
import com.manutencao.sistema_manutencao.entity.OrdemServico.Status;
import com.manutencao.sistema_manutencao.mapper.OrdemServicoMapper;
import com.manutencao.sistema_manutencao.service.ClienteService;
import com.manutencao.sistema_manutencao.service.OrdemServicoService;
import com.manutencao.sistema_manutencao.service.ServicoService;
import com.manutencao.sistema_manutencao.entity.OrdemServico.Status;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ordens")
public class OrdemServicoController {
    private final OrdemServicoService ordemServicoService;
    private final ClienteService clienteService;
    private final ServicoService servicoService;
    private final OrdemServicoMapper ordemServicoMapper;

    public OrdemServicoController(OrdemServicoService ordemServicoService,
                                  ClienteService clienteService,
                                  ServicoService servicoService,
                                  OrdemServicoMapper ordemServicoMapper) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.servicoService = servicoService;
        this.ordemServicoMapper = ordemServicoMapper;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ordens", ordemServicoService.listarTodas()
                .stream().map(ordemServicoMapper::toResponse).toList());
        return "ordens/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("ordemServicoRequest", new OrdemServicoRequest(null, null, null, null));
        model.addAttribute("clientes", clienteService.listarTodos());
        model.addAttribute("servicos", servicoService.listarTodos());
        return "ordens/form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("ordemServicoRequest") OrdemServicoRequest request,
                        BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteService.listarTodos());
            model.addAttribute("servicos", servicoService.listarTodos());
            return "ordens/form";
        }
        try {
            ordemServicoService.criar(request.clienteId(), request.descricaoProblema(),
                    request.servicoIds(), request.quantidades());
            redirect.addFlashAttribute("sucesso", "Ordem de serviço criada com sucesso!");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            return "redirect:/ordens/nova";
        }
        return "redirect:/ordens";
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable Long id,
                                @RequestParam Status status,
                                RedirectAttributes redirect) {
        try {
            ordemServicoService.alterarStatus(id, status);
            redirect.addFlashAttribute("sucesso", "Status atualizado para " + status);
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/ordens/" + id;
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model) {
        model.addAttribute("ordem", ordemServicoMapper.toResponse(ordemServicoService.buscarPorId(id)));
        return "ordens/detalhes";
    }
}