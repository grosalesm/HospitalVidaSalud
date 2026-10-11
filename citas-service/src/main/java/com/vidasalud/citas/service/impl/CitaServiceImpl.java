package com.vidasalud.citas.service.impl;

import com.vidasalud.citas.client.PacienteClient;
import com.vidasalud.citas.config.RabbitMQProperties;
import com.vidasalud.citas.dto.CitaRequestDTO;
import com.vidasalud.citas.dto.CitaResponseDTO;
import com.vidasalud.citas.entity.Cita;
import com.vidasalud.citas.event.CitaConfirmadaEvent;
import com.vidasalud.citas.exception.*;
import com.vidasalud.citas.mapper.CitaMapper;
import com.vidasalud.citas.repository.CitaRepository;
import com.vidasalud.citas.service.CitaService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final PacienteClient pacienteClient;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;

    @Override
    public Mono<CitaResponseDTO> programar(CitaRequestDTO requestDTO) {
        return pacienteClient.obtenerPaciente(requestDTO.pacienteId())
                .switchIfEmpty(Mono.error(new PacienteNoValidoException(
                        "El paciente con id " + requestDTO.pacienteId() + " no existe")))
                .flatMap(paciente -> {
                    if (!"ACTIVO".equals(paciente.estado())) {
                        return Mono.error(new PacienteNoValidoException("El paciente no está ACTIVO"));
                    }
                    return citaRepository
                            .findByPacienteIdAndFechaHora(requestDTO.pacienteId(), requestDTO.fechaHora())
                            .flatMap(existente -> Mono.error(new CitaDuplicadaException(
                                    "Ya existe una cita para ese paciente en la misma fecha y hora")))
                            .switchIfEmpty(Mono.defer(() -> {
                                Cita cita = citaMapper.toEntity(requestDTO);
                                cita.setEstado("PENDIENTE");
                                cita.setFechaCreacion(LocalDateTime.now());
                                return citaRepository.save(cita);
                            }))
                            .cast(Cita.class);
                })
                .map(citaMapper::toResponseDTO);
    }

    @Override
    public Flux<CitaResponseDTO> listar() {
        return citaRepository.findAll().map(citaMapper::toResponseDTO);
    }

    @Override
    public Mono<CitaResponseDTO> obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(new CitaNotFoundException("Cita no encontrada con id: " + id)))
                .map(citaMapper::toResponseDTO);
    }

    @Override
    public Flux<CitaResponseDTO> listarPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteId(pacienteId).map(citaMapper::toResponseDTO);
    }

    @Override
    public Mono<CitaResponseDTO> confirmar(Long id) {
        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(new CitaNotFoundException("Cita no encontrada con id: " + id)))
                .flatMap(cita -> {
                    if (!"PENDIENTE".equals(cita.getEstado())) {
                        return Mono.error(new TransicionEstadoInvalidaException(
                                "Solo se puede confirmar una cita PENDIENTE. Estado actual: " + cita.getEstado()));
                    }
                    cita.setEstado("CONFIRMADA");
                    return citaRepository.save(cita);
                })
                .flatMap(cita -> {
                    CitaConfirmadaEvent evento = new CitaConfirmadaEvent(
                            cita.getId(),
                            cita.getPacienteId(),
                            cita.getEspecialidad(),
                            cita.getFechaHora(),
                            cita.getEstado(),
                            LocalDateTime.now()
                    );
                    return Mono.fromRunnable(() -> rabbitTemplate.convertAndSend(
                                    rabbitMQProperties.getExchange(),
                                    rabbitMQProperties.getRoutingKeyCitaConfirmada(),
                                    evento))
                            .subscribeOn(Schedulers.boundedElastic())
                            .thenReturn(citaMapper.toResponseDTO(cita));
                });
    }

    @Override
    public Mono<CitaResponseDTO> atender(Long id) {
        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(new CitaNotFoundException("Cita no encontrada con id: " + id)))
                .flatMap(cita -> {
                    if (!"CONFIRMADA".equals(cita.getEstado())) {
                        return Mono.error(new TransicionEstadoInvalidaException(
                                "Solo se puede atender una cita CONFIRMADA. Estado actual: " + cita.getEstado()));
                    }
                    cita.setEstado("ATENDIDA");
                    return citaRepository.save(cita);
                })
                .map(citaMapper::toResponseDTO);
    }

    @Override
    public Mono<CitaResponseDTO> cancelar(Long id) {
        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(new CitaNotFoundException("Cita no encontrada con id: " + id)))
                .flatMap(cita -> {
                    if ("ATENDIDA".equals(cita.getEstado())) {
                        return Mono.error(new TransicionEstadoInvalidaException(
                                "No se puede cancelar una cita ya ATENDIDA"));
                    }
                    cita.setEstado("CANCELADA");
                    return citaRepository.save(cita);
                })
                .map(citaMapper::toResponseDTO);
    }

    @Override
    public Mono<CitaResponseDTO> reprogramar(Long id, CitaRequestDTO requestDTO) {
        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(new CitaNotFoundException("Cita no encontrada con id: " + id)))
                .flatMap(cita -> {
                    if ("ATENDIDA".equals(cita.getEstado()) || "CANCELADA".equals(cita.getEstado())) {
                        return Mono.error(new TransicionEstadoInvalidaException(
                                "No se puede reprogramar una cita " + cita.getEstado()));
                    }
                    return citaRepository
                            .findByPacienteIdAndFechaHora(cita.getPacienteId(), requestDTO.fechaHora())
                            .flatMap(existente -> {
                                if (!existente.getId().equals(id)) {
                                    return Mono.error(new CitaDuplicadaException(
                                            "Ya existe otra cita para ese paciente en la misma fecha y hora"));
                                }
                                cita.setFechaHora(requestDTO.fechaHora());
                                cita.setEspecialidad(requestDTO.especialidad());
                                return citaRepository.save(cita);
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                cita.setFechaHora(requestDTO.fechaHora());
                                cita.setEspecialidad(requestDTO.especialidad());
                                return citaRepository.save(cita);
                            }))
                            .cast(Cita.class);
                })
                .map(citaMapper::toResponseDTO);
    }

    @Override
    public Mono<CitaResponseDTO> marcarAusente(Long id) {
        return citaRepository.findById(id)
                .switchIfEmpty(Mono.error(new CitaNotFoundException("Cita no encontrada con id: " + id)))
                .flatMap(cita -> {
                    if ("ATENDIDA".equals(cita.getEstado()) || "CANCELADA".equals(cita.getEstado())) {
                        return Mono.error(new TransicionEstadoInvalidaException(
                                "No se puede marcar como AUSENTE una cita " + cita.getEstado()));
                    }
                    cita.setEstado("AUSENTE");
                    return citaRepository.save(cita);
                })
                .map(citaMapper::toResponseDTO);
    }
}