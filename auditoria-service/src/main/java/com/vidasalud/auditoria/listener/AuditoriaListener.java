package com.vidasalud.auditoria.listener;

import com.vidasalud.auditoria.entity.RegistroAuditoria;
import com.vidasalud.auditoria.event.CitaConfirmadaEvent;
import com.vidasalud.auditoria.service.AuditoriaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class AuditoriaListener {

    private final AuditoriaService auditoriaService;

    @RabbitListener(queues = "${app.rabbitmq.queue-auditoria}")
    public void recibirCitaConfirmada(CitaConfirmadaEvent evento) {
        log.info(">>> [AUDITORIA] Evento recibido: CITA_CONFIRMADA - citaId={}, pacienteId={}",
                evento.citaId(), evento.pacienteId());

        RegistroAuditoria registro = RegistroAuditoria.builder()
                .tipoEvento("CITA_CONFIRMADA")
                .tipoRecurso("CITA")
                .recursoId(evento.citaId())
                .pacienteId(evento.pacienteId())
                .estadoAnterior("PENDIENTE")
                .estadoNuevo(evento.estado())
                .origen("citas-service")
                .descripcion(String.format(
                        "Cita %d confirmada para paciente %d — especialidad: %s, fecha: %s",
                        evento.citaId(), evento.pacienteId(), evento.especialidad(), evento.fechaHora()))
                .fechaEvento(evento.fechaEvento())
                .build();

        auditoriaService.registrar(registro)
                .doOnSuccess(r -> log.info(">>> [AUDITORIA] Registro guardado con id={}", r.getId()))
                .doOnError(e -> log.error(">>> [AUDITORIA] Error al registrar auditoría: {}", e.getMessage()))
                .subscribe();
    }
}