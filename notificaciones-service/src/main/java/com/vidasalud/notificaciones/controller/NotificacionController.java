package com.vidasalud.notificaciones.controller;

import com.vidasalud.notificaciones.dto.NotificacionResponseDTO;
import com.vidasalud.notificaciones.service.NotificacionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notificaciones")
@AllArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public Flux<NotificacionResponseDTO> listar() {
        return notificacionService.listar();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<NotificacionResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return notificacionService.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/paciente/{pacienteId}")
    public Flux<NotificacionResponseDTO> listarPorPaciente(@PathVariable Long pacienteId) {
        return notificacionService.listarPorPaciente(pacienteId);
    }

    @GetMapping("/tipo/{tipoEvento}")
    public Flux<NotificacionResponseDTO> listarPorTipo(@PathVariable String tipoEvento) {
        return notificacionService.listarPorTipoEvento(tipoEvento);
    }
}
