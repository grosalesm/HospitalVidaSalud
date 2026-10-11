package com.vidasalud.citas.controller;

import com.vidasalud.citas.dto.CitaRequestDTO;
import com.vidasalud.citas.dto.CitaResponseDTO;
import com.vidasalud.citas.service.CitaService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/citas")
@AllArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping
    public Mono<ResponseEntity<CitaResponseDTO>> programar(
            @Valid @RequestBody CitaRequestDTO requestDTO) {
        return citaService.programar(requestDTO)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping
    public Flux<CitaResponseDTO> listar() {
        return citaService.listar();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<CitaResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return citaService.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/paciente/{pacienteId}")
    public Flux<CitaResponseDTO> listarPorPaciente(@PathVariable Long pacienteId) {
        return citaService.listarPorPaciente(pacienteId);
    }

    @PatchMapping("/{id}/confirmar")
    public Mono<ResponseEntity<CitaResponseDTO>> confirmar(@PathVariable Long id) {
        return citaService.confirmar(id)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/atender")
    public Mono<ResponseEntity<CitaResponseDTO>> atender(@PathVariable Long id) {
        return citaService.atender(id)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/cancelar")
    public Mono<ResponseEntity<CitaResponseDTO>> cancelar(@PathVariable Long id) {
        return citaService.cancelar(id)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}/reprogramar")
    public Mono<ResponseEntity<CitaResponseDTO>> reprogramar(
            @PathVariable Long id,
            @Valid @RequestBody CitaRequestDTO requestDTO) {
        return citaService.reprogramar(id, requestDTO)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/ausente")
    public Mono<ResponseEntity<CitaResponseDTO>> marcarAusente(@PathVariable Long id) {
        return citaService.marcarAusente(id)
                .map(ResponseEntity::ok);
    }
}