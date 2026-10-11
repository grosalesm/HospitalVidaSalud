package com.vidasalud.notificaciones.dto;

public record PacienteDTO(
        Long id,
        String tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String estado
) {}