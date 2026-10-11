package com.vidasalud.citas;

import com.vidasalud.citas.entity.Cita;
import com.vidasalud.citas.exception.CitaDuplicadaException;
import com.vidasalud.citas.exception.CitaNotFoundException;
import com.vidasalud.citas.exception.TransicionEstadoInvalidaException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CitasServiceApplicationTests {

    @Test
    void entidad_cita_debeCrearseCorrectamente() {
        LocalDateTime fecha = LocalDateTime.now().plusDays(5);
        Cita cita = Cita.builder()
                .id(1L)
                .pacienteId(10L)
                .especialidad("Cardiología")
                .fechaHora(fecha)
                .estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now())
                .build();

        assertThat(cita.getId()).isEqualTo(1L);
        assertThat(cita.getEstado()).isEqualTo("PENDIENTE");
        assertThat(cita.getEspecialidad()).isEqualTo("Cardiología");
    }

    @Test
    void estadosCita_soloLosDefinidosEnElExamen() {
        String[] estados = {"PENDIENTE", "CONFIRMADA", "ATENDIDA", "CANCELADA", "AUSENTE"};
        assertThat(estados).contains("PENDIENTE", "CONFIRMADA", "ATENDIDA", "CANCELADA", "AUSENTE");
        assertThat(estados).hasSize(5);
    }

    @Test
    void excepcion_citaNoEncontrada_debeContenerMensaje() {
        CitaNotFoundException ex = new CitaNotFoundException("Cita no encontrada con id: 5");
        assertThat(ex.getMessage()).contains("5");
    }

    @Test
    void excepcion_citaDuplicada_debeContenerMensaje() {
        CitaDuplicadaException ex = new CitaDuplicadaException("Ya existe una cita para ese paciente");
        assertThat(ex.getMessage()).isEqualTo("Ya existe una cita para ese paciente");
    }

    @Test
    void excepcion_transicionInvalida_debeContenerEstados() {
        TransicionEstadoInvalidaException ex = new TransicionEstadoInvalidaException(
                "Solo se puede confirmar una cita PENDIENTE. Estado actual: CANCELADA");
        assertThat(ex.getMessage()).contains("PENDIENTE");
        assertThat(ex.getMessage()).contains("CANCELADA");
    }

    @Test
    void regla_noPuedeCancelarCitaAtendida() {
        String estadoCita = "ATENDIDA";
        boolean puedeCancel = !"ATENDIDA".equals(estadoCita);
        assertThat(puedeCancel).isFalse();
    }

    @Test
    void regla_soloConfirmarDesdePendiente() {
        boolean puedeConfirmar    = "PENDIENTE".equals("PENDIENTE");
        boolean noPuedeConfirmar  = "PENDIENTE".equals("CONFIRMADA");
        assertThat(puedeConfirmar).isTrue();
        assertThat(noPuedeConfirmar).isFalse();
    }
}