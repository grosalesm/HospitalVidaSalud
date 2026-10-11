package com.vidasalud.auditoria.controller;

import com.vidasalud.auditoria.dto.AuditoriaResponseDTO;
import com.vidasalud.auditoria.service.AuditoriaService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auditoria")
@AllArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public Flux<AuditoriaResponseDTO> listar() {
        return auditoriaService.listar();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AuditoriaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return auditoriaService.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/recurso/{tipoRecurso}/{recursoId}")
    public Flux<AuditoriaResponseDTO> listarPorRecurso(
            @PathVariable String tipoRecurso,
            @PathVariable Long recursoId) {
        return auditoriaService.listarPorRecurso(tipoRecurso, recursoId);
    }

    @GetMapping("/paciente/{pacienteId}")
    public Flux<AuditoriaResponseDTO> listarPorPaciente(@PathVariable Long pacienteId) {
        return auditoriaService.listarPorPaciente(pacienteId);
    }

    @GetMapping("/evento/{tipoEvento}")
    public Flux<AuditoriaResponseDTO> listarPorTipoEvento(@PathVariable String tipoEvento) {
        return auditoriaService.listarPorTipoEvento(tipoEvento);
    }

    @GetMapping("/recurso/{tipoRecurso}")
    public Flux<AuditoriaResponseDTO> listarPorTipoRecurso(@PathVariable String tipoRecurso) {
        return auditoriaService.listarPorTipoRecurso(tipoRecurso);
    }
}