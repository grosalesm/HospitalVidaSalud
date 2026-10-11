package com.vidasalud.auditoria.dto;

import java.time.LocalDateTime;

public record AuditoriaResponseDTO(
        Long id,
        String tipoEvento,
        String tipoRecurso,
        Long recursoId,
        Long pacienteId,
        String estadoAnterior,
        String estadoNuevo,
        String origen,
        String descripcion,
        LocalDateTime fechaEvento
) {}