package com.vidasalud.auditoria.service;

import com.vidasalud.auditoria.dto.AuditoriaResponseDTO;
import com.vidasalud.auditoria.entity.RegistroAuditoria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AuditoriaService {
    Mono<RegistroAuditoria> registrar(RegistroAuditoria registro);
    Flux<AuditoriaResponseDTO> listar();
    Mono<AuditoriaResponseDTO> obtenerPorId(Long id);
    Flux<AuditoriaResponseDTO> listarPorRecurso(String tipoRecurso, Long recursoId);
    Flux<AuditoriaResponseDTO> listarPorPaciente(Long pacienteId);
    Flux<AuditoriaResponseDTO> listarPorTipoEvento(String tipoEvento);
    Flux<AuditoriaResponseDTO> listarPorTipoRecurso(String tipoRecurso);
}