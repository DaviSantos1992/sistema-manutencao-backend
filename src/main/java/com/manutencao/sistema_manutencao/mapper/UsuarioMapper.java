package com.manutencao.sistema_manutencao.mapper;

import com.manutencao.sistema_manutencao.dto.UsuarioResponse;
import com.manutencao.sistema_manutencao.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponse toResponse(Usuario usuario);
}