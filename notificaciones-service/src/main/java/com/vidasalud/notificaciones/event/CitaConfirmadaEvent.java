package com.vidasalud.notificaciones.event;

import java.time.LocalDateTime;

public record CitaConfirmadaEvent(
        Long citaId,
        Long pacienteId,
        String especialidad,
        LocalDateTime fechaHora,
        String estado,
        LocalDateTime fechaEvento
) {}
