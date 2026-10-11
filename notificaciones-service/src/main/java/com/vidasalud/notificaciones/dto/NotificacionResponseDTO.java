package com.vidasalud.notificaciones.dto;

import java.time.LocalDateTime;

public record NotificacionResponseDTO(
        Long id,
        String tipoEvento,
        Long pacienteId,
        Long referenciaId,
        String mensaje,
        String estado,
        LocalDateTime fechaCreacion
) {}