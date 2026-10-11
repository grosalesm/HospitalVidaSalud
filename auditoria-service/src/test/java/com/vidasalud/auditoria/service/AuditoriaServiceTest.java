package com.vidasalud.auditoria.service;

import com.vidasalud.auditoria.dto.AuditoriaResponseDTO;
import com.vidasalud.auditoria.entity.RegistroAuditoria;
import com.vidasalud.auditoria.exception.AuditoriaNotFoundException;
import com.vidasalud.auditoria.mapper.AuditoriaMapper;
import com.vidasalud.auditoria.repository.AuditoriaRepository;
import com.vidasalud.auditoria.service.impl.AuditoriaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditoriaServiceTest {

    @Mock
    private AuditoriaRepository auditoriaRepository;

    @Mock
    private AuditoriaMapper auditoriaMapper;

    @InjectMocks
    private AuditoriaServiceImpl auditoriaService;

    private RegistroAuditoria registro;
    private AuditoriaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        LocalDateTime ahora = LocalDateTime.now();
        registro = RegistroAuditoria.builder()
                .id(1L)
                .tipoEvento("CITA_CONFIRMADA")
                .tipoRecurso("CITA")
                .recursoId(5L)
                .pacienteId(10L)
                .estadoAnterior("PENDIENTE")
                .estadoNuevo("CONFIRMADA")
                .origen("citas-service")
                .descripcion("Cita 5 confirmada para paciente 10")
                .fechaEvento(ahora)
                .build();

        responseDTO = new AuditoriaResponseDTO(
                1L, "CITA_CONFIRMADA", "CITA", 5L, 10L,
                "PENDIENTE", "CONFIRMADA", "citas-service",
                "Cita 5 confirmada para paciente 10", ahora
        );
    }

    @Test
    void registrar_debeGuardarRegistroCorrectamente() {
        when(auditoriaRepository.save(any(RegistroAuditoria.class))).thenReturn(Mono.just(registro));

        StepVerifier.create(auditoriaService.registrar(registro))
                .expectNextMatches(r -> r.getId().equals(1L)
                        && r.getTipoEvento().equals("CITA_CONFIRMADA")
                        && r.getEstadoAnterior().equals("PENDIENTE")
                        && r.getEstadoNuevo().equals("CONFIRMADA"))
                .verifyComplete();
    }

    @Test
    void listar_debeRetornarTodosLosRegistros() {
        when(auditoriaRepository.findAll()).thenReturn(Flux.just(registro));
        when(auditoriaMapper.toResponseDTO(registro)).thenReturn(responseDTO);

        StepVerifier.create(auditoriaService.listar())
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarRegistro() {
        when(auditoriaRepository.findById(1L)).thenReturn(Mono.just(registro));
        when(auditoriaMapper.toResponseDTO(registro)).thenReturn(responseDTO);

        StepVerifier.create(auditoriaService.obtenerPorId(1L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarAuditoriaNotFoundException() {
        when(auditoriaRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(auditoriaService.obtenerPorId(99L))
                .expectError(AuditoriaNotFoundException.class)
                .verify();
    }

    @Test
    void listarPorRecurso_debeRetornarRegistrosDeLaCita() {
        when(auditoriaRepository.findByTipoRecursoAndRecursoId("CITA", 5L))
                .thenReturn(Flux.just(registro));
        when(auditoriaMapper.toResponseDTO(registro)).thenReturn(responseDTO);

        StepVerifier.create(auditoriaService.listarPorRecurso("CITA", 5L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void listarPorPaciente_debeRetornarRegistrosDelPaciente() {
        when(auditoriaRepository.findByPacienteId(10L)).thenReturn(Flux.just(registro));
        when(auditoriaMapper.toResponseDTO(registro)).thenReturn(responseDTO);

        StepVerifier.create(auditoriaService.listarPorPaciente(10L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void listarPorTipoEvento_debeRetornarRegistrosFiltradosPorEvento() {
        when(auditoriaRepository.findByTipoEvento("CITA_CONFIRMADA"))
                .thenReturn(Flux.just(registro));
        when(auditoriaMapper.toResponseDTO(registro)).thenReturn(responseDTO);

        StepVerifier.create(auditoriaService.listarPorTipoEvento("CITA_CONFIRMADA"))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void listarPorTipoRecurso_debeRetornarRegistrosDeTipoCita() {
        when(auditoriaRepository.findByTipoRecurso("CITA")).thenReturn(Flux.just(registro));
        when(auditoriaMapper.toResponseDTO(registro)).thenReturn(responseDTO);

        StepVerifier.create(auditoriaService.listarPorTipoRecurso("CITA"))
                .expectNext(responseDTO)
                .verifyComplete();
    }
}