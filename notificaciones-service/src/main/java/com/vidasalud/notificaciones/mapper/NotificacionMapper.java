package com.vidasalud.notificaciones.mapper;

import com.vidasalud.notificaciones.dto.NotificacionResponseDTO;
import com.vidasalud.notificaciones.entity.Notificacion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificacionMapper {
    NotificacionResponseDTO toResponseDTO(Notificacion notificacion);
}