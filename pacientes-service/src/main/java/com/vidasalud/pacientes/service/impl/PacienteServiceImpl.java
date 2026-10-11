package com.vidasalud.pacientes.service.impl;

import com.vidasalud.pacientes.dto.PacienteRequestDTO;
import com.vidasalud.pacientes.dto.PacienteResponseDTO;
import com.vidasalud.pacientes.entity.Paciente;
import com.vidasalud.pacientes.exception.PacienteNotFoundException;
import com.vidasalud.pacientes.exception.PacienteDuplicadoException;
import com.vidasalud.pacientes.mapper.PacienteMapper;
import com.vidasalud.pacientes.repository.PacienteRepository;
import com.vidasalud.pacientes.service.PacienteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PacienteServiceImpl implements PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteMapper pacienteMapper;

    @Override
    public Mono<PacienteResponseDTO> registrar(PacienteRequestDTO requestDTO) {
        return pacienteRepository
                .findByTipoDocumentoAndNumeroDocumento(
                        requestDTO.tipoDocumento(),
                        requestDTO.numeroDocumento())
                .flatMap(existente -> Mono.error(
                        new PacienteDuplicadoException("Ya existe un paciente con ese documento")))
                .switchIfEmpty(Mono.defer(() -> {
                    Paciente paciente = pacienteMapper.toEntity(requestDTO);
                    paciente.setFechaRegistro(LocalDateTime.now());
                    paciente.setEstado("ACTIVO");
                    return pacienteRepository.save(paciente);
                }))
                .cast(Paciente.class)
                .map(pacienteMapper::toResponseDTO);
    }

    @Override
    public Flux<PacienteResponseDTO> listar() {
        return pacienteRepository.findAll()
                .map(pacienteMapper::toResponseDTO);
    }

    @Override
    public Mono<PacienteResponseDTO> obtenerPorId(Long id) {
        return pacienteRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new PacienteNotFoundException("Paciente no encontrado con id: " + id)))
                .map(pacienteMapper::toResponseDTO);
    }

    @Override
    public Mono<PacienteResponseDTO> obtenerPorDocumento(String tipoDocumento, String numeroDocumento) {
        return pacienteRepository
                .findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .switchIfEmpty(Mono.error(
                        new PacienteNotFoundException("Paciente no encontrado con documento: "
                                + tipoDocumento + " " + numeroDocumento)))
                .map(pacienteMapper::toResponseDTO);
    }

    @Override
    public Mono<PacienteResponseDTO> actualizar(Long id, PacienteRequestDTO requestDTO) {
        return pacienteRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new PacienteNotFoundException("Paciente no encontrado con id: " + id)))
                .flatMap(paciente -> {
                    pacienteMapper.updateEntityFromDTO(requestDTO, paciente);
                    return pacienteRepository.save(paciente);
                })
                .map(pacienteMapper::toResponseDTO);
    }

    @Override
    public Mono<PacienteResponseDTO> cambiarEstado(Long id, String nuevoEstado) {
        if (!"ACTIVO".equals(nuevoEstado) && !"INACTIVO".equals(nuevoEstado)) {
            return Mono.error(new IllegalArgumentException(
                    "Estado inválido '" + nuevoEstado + "'. Valores permitidos: ACTIVO, INACTIVO"));
        }
        return pacienteRepository.findById(id)
                .switchIfEmpty(Mono.error(
                        new PacienteNotFoundException("Paciente no encontrado con id: " + id)))
                .flatMap(paciente -> {
                    paciente.setEstado(nuevoEstado);
                    return pacienteRepository.save(paciente);
                })
                .map(pacienteMapper::toResponseDTO);
    }
}