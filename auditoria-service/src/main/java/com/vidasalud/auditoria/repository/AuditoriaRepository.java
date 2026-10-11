package com.vidasalud.auditoria.repository;

import com.vidasalud.auditoria.entity.RegistroAuditoria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AuditoriaRepository extends ReactiveCrudRepository<RegistroAuditoria, Long> {
    Flux<RegistroAuditoria> findByTipoRecursoAndRecursoId(String tipoRecurso, Long recursoId);
    Flux<RegistroAuditoria> findByPacienteId(Long pacienteId);
    Flux<RegistroAuditoria> findByTipoEvento(String tipoEvento);
    Flux<RegistroAuditoria> findByTipoRecurso(String tipoRecurso);
}