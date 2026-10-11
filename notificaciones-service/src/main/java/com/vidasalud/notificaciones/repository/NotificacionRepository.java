package com.vidasalud.notificaciones.repository;

import com.vidasalud.notificaciones.entity.Notificacion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface NotificacionRepository extends ReactiveCrudRepository<Notificacion, Long> {
    Flux<Notificacion> findByPacienteId(Long pacienteId);
    Flux<Notificacion> findByTipoEvento(String tipoEvento);
}