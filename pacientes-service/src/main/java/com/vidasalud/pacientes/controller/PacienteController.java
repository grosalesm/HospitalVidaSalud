package com.vidasalud.pacientes.controller;

import com.vidasalud.pacientes.dto.PacienteRequestDTO;
import com.vidasalud.pacientes.dto.PacienteResponseDTO;
import com.vidasalud.pacientes.entity.Paciente;
import com.vidasalud.pacientes.repository.PacienteRepository;
import com.vidasalud.pacientes.service.PacienteService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/pacientes")
@AllArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final PacienteRepository pacienteRepository;

    @PostMapping
    public Mono<ResponseEntity<PacienteResponseDTO>> registrar(
            @Valid @RequestBody PacienteRequestDTO requestDTO) {
        return pacienteService.registrar(requestDTO)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping
    public Flux<PacienteResponseDTO> listar() {
        return pacienteService.listar();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PacienteResponseDTO>> obtenerPorId(@PathVariable Long id) {
        return pacienteService.obtenerPorId(id)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/documento/{tipo}/{numero}")
    public Mono<ResponseEntity<PacienteResponseDTO>> obtenerPorDocumento(
            @PathVariable String tipo,
            @PathVariable String numero) {
        return pacienteService.obtenerPorDocumento(tipo, numero)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PacienteResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PacienteRequestDTO requestDTO) {
        return pacienteService.actualizar(id, requestDTO)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{id}/estado")
    public Mono<ResponseEntity<PacienteResponseDTO>> cambiarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return pacienteService.cambiarEstado(id, estado)
                .map(ResponseEntity::ok);
    }

}