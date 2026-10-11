package com.vidasalud.pacientes.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PacienteResponseDTO(
        Long id,
        String tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        LocalDate fechaNacimiento,
        String telefono,
        String correo,
        String direccion,
        LocalDateTime fechaRegistro,
        String estado
) {
}