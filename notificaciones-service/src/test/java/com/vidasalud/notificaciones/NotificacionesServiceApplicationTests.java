package com.vidasalud.notificaciones;

import com.vidasalud.notificaciones.entity.Notificacion;
import com.vidasalud.notificaciones.event.CitaConfirmadaEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class NotificacionesServiceApplicationTests {

    @Test
    void entidad_notificacion_debeCrearseCorrectamente() {
        Notificacion notificacion = Notificacion.builder()
                .tipoEvento("CITA_CONFIRMADA")
                .pacienteId(1L)
                .referenciaId(2L)
                .mensaje("Cita confirmada")
                .estado("ENVIADA")
                .fechaCreacion(LocalDateTime.now())
                .build();

        assertThat(notificacion.getTipoEvento()).isEqualTo("CITA_CONFIRMADA");
        assertThat(notificacion.getEstado()).isEqualTo("ENVIADA");
        assertThat(notificacion.getPacienteId()).isEqualTo(1L);
    }

    @Test
    void evento_citaConfirmada_debeCrearseComoRecord() {
        LocalDateTime fecha = LocalDateTime.of(2024, 12, 1, 9, 0);
        CitaConfirmadaEvent evento = new CitaConfirmadaEvent(
                5L, 10L, "Cardiología", fecha, "CONFIRMADA", LocalDateTime.now()
        );

        assertThat(evento.citaId()).isEqualTo(5L);
        assertThat(evento.pacienteId()).isEqualTo(10L);
        assertThat(evento.especialidad()).isEqualTo("Cardiología");
        assertThat(evento.estado()).isEqualTo("CONFIRMADA");
    }
}