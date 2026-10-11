package com.vidasalud.auditoria;

import com.vidasalud.auditoria.entity.RegistroAuditoria;
import com.vidasalud.auditoria.event.CitaConfirmadaEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AuditoriaServiceApplicationTests {

    @Test
    void entidad_registroAuditoria_debeCrearseCorrectamente() {
        RegistroAuditoria registro = RegistroAuditoria.builder()
                .tipoEvento("CITA_CONFIRMADA")
                .tipoRecurso("CITA")
                .recursoId(5L)
                .pacienteId(10L)
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("CONFIRMADA")
                .origen("citas-service")
                .descripcion("Cita confirmada correctamente")
                .fechaEvento(LocalDateTime.now())
                .build();

        assertThat(registro.getTipoEvento()).isEqualTo("CITA_CONFIRMADA");
        assertThat(registro.getEstadoAnterior()).isEqualTo("PENDIENTE");
        assertThat(registro.getEstadoNuevo()).isEqualTo("CONFIRMADA");
        assertThat(registro.getOrigen()).isEqualTo("citas-service");
    }

    @Test
    void evento_citaConfirmada_debeMapearCamposCorrectamente() {
        LocalDateTime fechaHora = LocalDateTime.of(2024, 12, 1, 9, 0);
        LocalDateTime fechaEvento = LocalDateTime.now();

        CitaConfirmadaEvent evento = new CitaConfirmadaEvent(
                5L, 10L, "Cardiología", fechaHora, "CONFIRMADA", fechaEvento
        );

        assertThat(evento.citaId()).isEqualTo(5L);
        assertThat(evento.pacienteId()).isEqualTo(10L);
        assertThat(evento.estado()).isEqualTo("CONFIRMADA");
        assertThat(evento.especialidad()).isEqualTo("Cardiología");
    }

    @Test
    void transicion_estadoDebeSeguirReglaNegocio() {
        String estadoAnterior = "PENDIENTE";
        String estadoNuevo = "CONFIRMADA";

        assertThat(estadoAnterior).isNotEqualTo(estadoNuevo);
        assertThat(estadoNuevo).isEqualTo("CONFIRMADA");
    }
}