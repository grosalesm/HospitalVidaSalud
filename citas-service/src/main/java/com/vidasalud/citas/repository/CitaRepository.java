package com.vidasalud.citas.repository;

import com.vidasalud.citas.entity.Cita;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface CitaRepository extends ReactiveCrudRepository<Cita, Long> {

    Flux<Cita> findByPacienteId(Long pacienteId);
    Mono<Cita> findByPacienteIdAndFechaHora(Long pacienteId, LocalDateTime fechaHora);
    Flux<Cita> findByEstado(String estado);
}