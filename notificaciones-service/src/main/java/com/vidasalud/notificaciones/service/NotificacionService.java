package com.vidasalud.notificaciones.service;

import com.vidasalud.notificaciones.dto.NotificacionResponseDTO;
import com.vidasalud.notificaciones.entity.Notificacion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificacionService {
    Mono<Notificacion> guardar(Notificacion notificacion);
    Flux<NotificacionResponseDTO> listar();
    Mono<NotificacionResponseDTO> obtenerPorId(Long id);
    Flux<NotificacionResponseDTO> listarPorPaciente(Long pacienteId);
    Flux<NotificacionResponseDTO> listarPorTipoEvento(String tipoEvento);
}