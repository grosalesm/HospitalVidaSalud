package com.vidasalud.citas.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CitaRequestDTO(

        @NotNull(message = "El ID del paciente es obligatorio")
        Long pacienteId,

        @NotBlank(message = "La especialidad es obligatoria")
        String especialidad,

        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "La fecha y hora deben ser futuras")
        LocalDateTime fechaHora
) {
}