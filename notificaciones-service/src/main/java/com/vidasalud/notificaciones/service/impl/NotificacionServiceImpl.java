package com.vidasalud.notificaciones.service.impl;

import com.vidasalud.notificaciones.dto.NotificacionResponseDTO;
import com.vidasalud.notificaciones.entity.Notificacion;
import com.vidasalud.notificaciones.exception.NotificacionNotFoundException;
import com.vidasalud.notificaciones.mapper.NotificacionMapper;
import com.vidasalud.notificaciones.repository.NotificacionRepository;
import com.vidasalud.notificaciones.service.NotificacionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final NotificacionMapper notificacionMapper;

    @Override
    public Mono<Notificacion> guardar(Notificacion notificacion) {
        return notificacionRepository.save(notificacion);
    }

    @Override
    public Flux<NotificacionResponseDTO> listar() {
        return notificacionRepository.findAll()
                .map(notificacionMapper::toResponseDTO);
    }

    @Override
    public Mono<NotificacionResponseDTO> obtenerPorId(Long id) {
        return notificacionRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new NotificacionNotFoundException("Notificación no encontrada con id: " + id)))
                .map(notificacionMapper::toResponseDTO);
    }

    @Override
    public Flux<NotificacionResponseDTO> listarPorPaciente(Long pacienteId) {
        return notificacionRepository.findByPacienteId(pacienteId)
                .map(notificacionMapper::toResponseDTO);
    }

    @Override
    public Flux<NotificacionResponseDTO> listarPorTipoEvento(String tipoEvento) {
        return notificacionRepository.findByTipoEvento(tipoEvento)
                .map(notificacionMapper::toResponseDTO);
    }
}