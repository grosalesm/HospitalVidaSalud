package com.vidasalud.notificaciones.listener;

import com.vidasalud.notificaciones.client.PacienteClient;
import com.vidasalud.notificaciones.entity.Notificacion;
import com.vidasalud.notificaciones.event.CitaConfirmadaEvent;
import com.vidasalud.notificaciones.service.NotificacionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@AllArgsConstructor
public class NotificacionListener {

    private final NotificacionService notificacionService;
    private final PacienteClient pacienteClient;

    @RabbitListener(queues = "${app.rabbitmq.queue-notificaciones}")
    public void recibirCitaConfirmada(CitaConfirmadaEvent evento) {
        log.info(">>> [NOTIFICACIONES] Evento recibido: CITA_CONFIRMADA - citaId={}, pacienteId={}",
                evento.citaId(), evento.pacienteId());

        pacienteClient.obtenerPaciente(evento.pacienteId())
                .flatMap(paciente -> {
                    log.info(">>> [NOTIFICACIONES] Paciente válido: {} {}", paciente.nombres(), paciente.apellidos());
                    return notificacionService.guardar(buildNotificacion(evento, "ENVIADA"));
                })
                .switchIfEmpty(
                    notificacionService.guardar(buildNotificacion(evento, "FALLIDA"))
                        .doOnSuccess(n -> log.warn(
                            ">>> [NOTIFICACIONES] Paciente {} no encontrado — notificación guardada como FALLIDA",
                            evento.pacienteId()))
                )
                .doOnSuccess(n -> log.info(">>> [NOTIFICACIONES] Notificación id={} guardada con estado={}", n.getId(), n.getEstado()))
                .doOnError(e -> log.error(">>> [NOTIFICACIONES] Error al procesar evento: {}", e.getMessage()))
                .subscribe();
    }

    private Notificacion buildNotificacion(CitaConfirmadaEvent evento, String estado) {
        return Notificacion.builder()
                .tipoEvento("CITA_CONFIRMADA")
                .pacienteId(evento.pacienteId())
                .referenciaId(evento.citaId())
                .mensaje(String.format(
                        "Su cita ha sido confirmada. Especialidad: %s, Fecha: %s",
                        evento.especialidad(), evento.fechaHora()))
                .estado(estado)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }
}