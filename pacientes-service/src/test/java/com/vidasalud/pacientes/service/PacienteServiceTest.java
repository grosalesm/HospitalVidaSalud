package com.vidasalud.pacientes.service;

import com.vidasalud.pacientes.dto.PacienteRequestDTO;
import com.vidasalud.pacientes.dto.PacienteResponseDTO;
import com.vidasalud.pacientes.entity.Paciente;
import com.vidasalud.pacientes.exception.PacienteDuplicadoException;
import com.vidasalud.pacientes.exception.PacienteNotFoundException;
import com.vidasalud.pacientes.mapper.PacienteMapper;
import com.vidasalud.pacientes.repository.PacienteRepository;
import com.vidasalud.pacientes.service.impl.PacienteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;

    @Mock
    private PacienteMapper pacienteMapper;

    @InjectMocks
    private PacienteServiceImpl pacienteService;

    private PacienteRequestDTO requestDTO;
    private Paciente paciente;
    private PacienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new PacienteRequestDTO(
                "DNI", "12345678", "Juan", "Pérez",
                LocalDate.of(1990, 5, 15), "987654321",
                "juan@example.com", "Av. Lima 123"
        );

        paciente = Paciente.builder()
                .id(1L)
                .tipoDocumento("DNI")
                .numeroDocumento("12345678")
                .nombres("Juan")
                .apellidos("Pérez")
                .fechaNacimiento(LocalDate.of(1990, 5, 15))
                .telefono("987654321")
                .correo("juan@example.com")
                .estado("ACTIVO")
                .fechaRegistro(LocalDateTime.now())
                .build();

        responseDTO = new PacienteResponseDTO(
                1L, "DNI", "12345678", "Juan", "Pérez",
                LocalDate.of(1990, 5, 15), "987654321",
                "juan@example.com", "Av. Lima 123",
                paciente.getFechaRegistro(), "ACTIVO"
        );
    }

    @Test
    void registrar_conDatosValidos_debeGuardarPaciente() {
        when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento("DNI", "12345678"))
                .thenReturn(Mono.empty());
        when(pacienteMapper.toEntity(requestDTO)).thenReturn(paciente);
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(Mono.just(paciente));
        when(pacienteMapper.toResponseDTO(paciente)).thenReturn(responseDTO);

        StepVerifier.create(pacienteService.registrar(requestDTO))
                .expectNextMatches(r -> r.id().equals(1L)
                        && r.estado().equals("ACTIVO")
                        && r.tipoDocumento().equals("DNI"))
                .verifyComplete();
    }

    @Test
    void registrar_conDocumentoDuplicado_debeLanzarPacienteDuplicadoException() {
        when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento("DNI", "12345678"))
                .thenReturn(Mono.just(paciente));

        StepVerifier.create(pacienteService.registrar(requestDTO))
                .expectError(PacienteDuplicadoException.class)
                .verify();
    }

    @Test
    void listar_debeRetornarTodosLosPacientes() {
        Paciente p2 = Paciente.builder().id(2L).nombres("Ana").build();
        PacienteResponseDTO r2 = new PacienteResponseDTO(
                2L, "CE", "87654321", "Ana", "García",
                LocalDate.of(1985, 3, 20), null, null, null, null, "ACTIVO"
        );

        when(pacienteRepository.findAll()).thenReturn(Flux.just(paciente, p2));
        when(pacienteMapper.toResponseDTO(paciente)).thenReturn(responseDTO);
        when(pacienteMapper.toResponseDTO(p2)).thenReturn(r2);

        StepVerifier.create(pacienteService.listar())
                .expectNext(responseDTO)
                .expectNext(r2)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarPaciente() {
        when(pacienteRepository.findById(1L)).thenReturn(Mono.just(paciente));
        when(pacienteMapper.toResponseDTO(paciente)).thenReturn(responseDTO);

        StepVerifier.create(pacienteService.obtenerPorId(1L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarPacienteNotFoundException() {
        when(pacienteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(pacienteService.obtenerPorId(99L))
                .expectError(PacienteNotFoundException.class)
                .verify();
    }

    @Test
    void obtenerPorDocumento_cuandoExiste_debeRetornarPaciente() {
        when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento("DNI", "12345678"))
                .thenReturn(Mono.just(paciente));
        when(pacienteMapper.toResponseDTO(paciente)).thenReturn(responseDTO);

        StepVerifier.create(pacienteService.obtenerPorDocumento("DNI", "12345678"))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void obtenerPorDocumento_cuandoNoExiste_debeLanzarPacienteNotFoundException() {
        when(pacienteRepository.findByTipoDocumentoAndNumeroDocumento("DNI", "99999999"))
                .thenReturn(Mono.empty());

        StepVerifier.create(pacienteService.obtenerPorDocumento("DNI", "99999999"))
                .expectError(PacienteNotFoundException.class)
                .verify();
    }

    @Test
    void actualizar_cuandoExiste_debeActualizarDatos() {
        when(pacienteRepository.findById(1L)).thenReturn(Mono.just(paciente));
        doNothing().when(pacienteMapper).updateEntityFromDTO(eq(requestDTO), any(Paciente.class));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(Mono.just(paciente));
        when(pacienteMapper.toResponseDTO(paciente)).thenReturn(responseDTO);

        StepVerifier.create(pacienteService.actualizar(1L, requestDTO))
                .expectNext(responseDTO)
                .verifyComplete();

        verify(pacienteMapper).updateEntityFromDTO(eq(requestDTO), any(Paciente.class));
    }

    @Test
    void actualizar_cuandoNoExiste_debeLanzarPacienteNotFoundException() {
        when(pacienteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(pacienteService.actualizar(99L, requestDTO))
                .expectError(PacienteNotFoundException.class)
                .verify();
    }

    @Test
    void cambiarEstado_aInactivo_debeActualizarEstado() {
        when(pacienteRepository.findById(1L)).thenReturn(Mono.just(paciente));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(Mono.just(paciente));
        when(pacienteMapper.toResponseDTO(any(Paciente.class))).thenReturn(responseDTO);

        StepVerifier.create(pacienteService.cambiarEstado(1L, "INACTIVO"))
                .expectNext(responseDTO)
                .verifyComplete();

        verify(pacienteRepository).save(argThat(p -> "INACTIVO".equals(p.getEstado())));
    }

    @Test
    void cambiarEstado_cuandoNoExiste_debeLanzarPacienteNotFoundException() {
        when(pacienteRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(pacienteService.cambiarEstado(99L, "INACTIVO"))
                .expectError(PacienteNotFoundException.class)
                .verify();
    }
}