package com.vidasalud.citas.dto;

import java.time.LocalDateTime;

public record CitaResponseDTO(
        Long id,
        Long pacienteId,
        String especialidad,
        LocalDateTime fechaHora,
        String estado,
        LocalDateTime fechaCreacion
) {
}