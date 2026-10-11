package com.vidasalud.citas.service;

import com.vidasalud.citas.dto.CitaRequestDTO;
import com.vidasalud.citas.dto.CitaResponseDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CitaService {

    Mono<CitaResponseDTO> programar(CitaRequestDTO requestDTO);
    Flux<CitaResponseDTO> listar();
    Mono<CitaResponseDTO> obtenerPorId(Long id);
    Flux<CitaResponseDTO> listarPorPaciente(Long pacienteId);
    Mono<CitaResponseDTO> confirmar(Long id);
    Mono<CitaResponseDTO> atender(Long id);
    Mono<CitaResponseDTO> cancelar(Long id);
    Mono<CitaResponseDTO> reprogramar(Long id, CitaRequestDTO requestDTO);
    Mono<CitaResponseDTO> marcarAusente(Long id);
}