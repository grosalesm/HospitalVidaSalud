package com.vidasalud.pacientes.service;

import com.vidasalud.pacientes.dto.PacienteRequestDTO;
import com.vidasalud.pacientes.dto.PacienteResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PacienteService {

    Mono<PacienteResponseDTO> registrar(PacienteRequestDTO requestDTO);
    Flux<PacienteResponseDTO> listar();
    Mono<PacienteResponseDTO> obtenerPorId(Long id);
    Mono<PacienteResponseDTO> obtenerPorDocumento(String tipoDocumento, String numeroDocumento);
    Mono<PacienteResponseDTO> actualizar(Long id, PacienteRequestDTO requestDTO);
    Mono<PacienteResponseDTO> cambiarEstado(Long id, String nuevoEstado);
}