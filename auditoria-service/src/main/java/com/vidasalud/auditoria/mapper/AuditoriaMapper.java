package com.vidasalud.auditoria.mapper;

import com.vidasalud.auditoria.dto.AuditoriaResponseDTO;
import com.vidasalud.auditoria.entity.RegistroAuditoria;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuditoriaMapper {
    AuditoriaResponseDTO toResponseDTO(RegistroAuditoria registro);
}