package com.manutencao.sistema_manutencao.mapper;

import com.manutencao.sistema_manutencao.dto.OrdemServicoResponse;
import com.manutencao.sistema_manutencao.entity.OrdemServico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrdemServicoMapper {

    @Mapping(target = "clienteNome", source = "cliente.nome")
    OrdemServicoResponse toResponse(OrdemServico ordem);
}