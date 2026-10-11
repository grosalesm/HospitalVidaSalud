package com.vidasalud.citas.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CitaConfirmadaEvent(
        Long citaId,
        Long pacienteId,
        String especialidad,
        LocalDateTime fechaHora,
        String estado,
        LocalDateTime fechaEvento
) implements Serializable {
}