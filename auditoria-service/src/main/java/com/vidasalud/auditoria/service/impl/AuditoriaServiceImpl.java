package com.vidasalud.auditoria.service.impl;

import com.vidasalud.auditoria.dto.AuditoriaResponseDTO;
import com.vidasalud.auditoria.entity.RegistroAuditoria;
import com.vidasalud.auditoria.exception.AuditoriaNotFoundException;
import com.vidasalud.auditoria.mapper.AuditoriaMapper;
import com.vidasalud.auditoria.repository.AuditoriaRepository;
import com.vidasalud.auditoria.service.AuditoriaService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;

    @Override
    public Mono<RegistroAuditoria> registrar(RegistroAuditoria registro) {
        return auditoriaRepository.save(registro);
    }

    @Override
    public Flux<AuditoriaResponseDTO> listar() {
        return auditoriaRepository.findAll()
                .map(auditoriaMapper::toResponseDTO);
    }

    @Override
    public Mono<AuditoriaResponseDTO> obtenerPorId(Long id) {
        return auditoriaRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new AuditoriaNotFoundException("Registro de auditoría no encontrado con id: " + id)))
                .map(auditoriaMapper::toResponseDTO);
    }

    @Override
    public Flux<AuditoriaResponseDTO> listarPorRecurso(String tipoRecurso, Long recursoId) {
        return auditoriaRepository.findByTipoRecursoAndRecursoId(tipoRecurso, recursoId)
                .map(auditoriaMapper::toResponseDTO);
    }

    @Override
    public Flux<AuditoriaResponseDTO> listarPorPaciente(Long pacienteId) {
        return auditoriaRepository.findByPacienteId(pacienteId)
                .map(auditoriaMapper::toResponseDTO);
    }

    @Override
    public Flux<AuditoriaResponseDTO> listarPorTipoEvento(String tipoEvento) {
        return auditoriaRepository.findByTipoEvento(tipoEvento)
                .map(auditoriaMapper::toResponseDTO);
    }

    @Override
    public Flux<AuditoriaResponseDTO> listarPorTipoRecurso(String tipoRecurso) {
        return auditoriaRepository.findByTipoRecurso(tipoRecurso)
                .map(auditoriaMapper::toResponseDTO);
    }
}