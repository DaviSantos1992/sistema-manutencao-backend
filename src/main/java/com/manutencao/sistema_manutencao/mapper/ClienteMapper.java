package com.manutencao.sistema_manutencao.mapper;

import com.manutencao.sistema_manutencao.dto.ClienteRequest;
import com.manutencao.sistema_manutencao.dto.ClienteResponse;
import com.manutencao.sistema_manutencao.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "id", ignore = true)
    Cliente toEntity(ClienteRequest request);

    ClienteResponse toResponse(Cliente cliente);
}