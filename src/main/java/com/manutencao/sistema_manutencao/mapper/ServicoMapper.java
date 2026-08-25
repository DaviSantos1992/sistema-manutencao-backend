package com.manutencao.sistema_manutencao.mapper;

import com.manutencao.sistema_manutencao.dto.ServicoRequest;
import com.manutencao.sistema_manutencao.dto.ServicoResponse;
import com.manutencao.sistema_manutencao.entity.Servico;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServicoMapper {
    @Mapping(target = "id", ignore = true)
    Servico toEntity(ServicoRequest request);

    ServicoResponse toResponse(Servico servico);
}