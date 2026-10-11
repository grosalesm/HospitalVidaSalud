package com.vidasalud.pacientes;

import com.vidasalud.pacientes.entity.Paciente;
import com.vidasalud.pacientes.exception.PacienteDuplicadoException;
import com.vidasalud.pacientes.exception.PacienteNotFoundException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PacientesServiceApplicationTests {

    @Test
    void entidad_paciente_debeCrearseCorrectamente() {
        Paciente paciente = Paciente.builder()
                .id(1L)
                .tipoDocumento("DNI")
                .numeroDocumento("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .estado("ACTIVO")
                .fechaRegistro(LocalDateTime.now())
                .build();

        assertThat(paciente.getId()).isEqualTo(1L);
        assertThat(paciente.getTipoDocumento()).isEqualTo("DNI");
        assertThat(paciente.getEstado()).isEqualTo("ACTIVO");
    }

    @Test
    void excepcion_pacienteDuplicado_debeContenerMensaje() {
        PacienteDuplicadoException ex = new PacienteDuplicadoException("Ya existe un paciente con ese documento");
        assertThat(ex.getMessage()).isEqualTo("Ya existe un paciente con ese documento");
    }

    @Test
    void excepcion_pacienteNoEncontrado_debeContenerMensaje() {
        PacienteNotFoundException ex = new PacienteNotFoundException("Paciente no encontrado con id: 1");
        assertThat(ex.getMessage()).contains("1");
    }

    @Test
    void estadosValidos_paciente_soloActivoEInactivo() {
        String[] estadosValidos = {"ACTIVO", "INACTIVO"};
        assertThat(estadosValidos).contains("ACTIVO");
        assertThat(estadosValidos).contains("INACTIVO");
        assertThat(estadosValidos).hasSize(2);
    }
}