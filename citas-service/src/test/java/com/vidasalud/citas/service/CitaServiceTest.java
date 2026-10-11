package com.vidasalud.citas.service;

import com.vidasalud.citas.client.PacienteClient;
import com.vidasalud.citas.config.RabbitMQProperties;
import com.vidasalud.citas.dto.CitaRequestDTO;
import com.vidasalud.citas.dto.CitaResponseDTO;
import com.vidasalud.citas.dto.PacienteDTO;
import com.vidasalud.citas.entity.Cita;
import com.vidasalud.citas.exception.CitaDuplicadaException;
import com.vidasalud.citas.exception.CitaNotFoundException;
import com.vidasalud.citas.exception.PacienteNoValidoException;
import com.vidasalud.citas.exception.TransicionEstadoInvalidaException;
import com.vidasalud.citas.mapper.CitaMapper;
import com.vidasalud.citas.repository.CitaRepository;
import com.vidasalud.citas.service.impl.CitaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import com.vidasalud.citas.event.CitaConfirmadaEvent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceTest {

    @Mock private CitaRepository citaRepository;
    @Mock private CitaMapper citaMapper;
    @Mock private PacienteClient pacienteClient;
    @Mock private RabbitTemplate rabbitTemplate;
    @Mock private RabbitMQProperties rabbitMQProperties;

    @InjectMocks
    private CitaServiceImpl citaService;

    private final LocalDateTime fechaFutura = LocalDateTime.now().plusDays(10);

    private CitaRequestDTO requestDTO;
    private Cita citaPendiente;
    private Cita citaConfirmada;
    private Cita citaAtendida;
    private Cita citaCancelada;
    private CitaResponseDTO responseDTO;
    private PacienteDTO pacienteActivo;
    private PacienteDTO pacienteInactivo;

    @BeforeEach
    void setUp() {
        requestDTO = new CitaRequestDTO(1L, "Cardiología", fechaFutura);

        citaPendiente = Cita.builder()
                .id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now()).build();

        citaConfirmada = Cita.builder()
                .id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("CONFIRMADA")
                .fechaCreacion(LocalDateTime.now()).build();

        citaAtendida = Cita.builder()
                .id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("ATENDIDA")
                .fechaCreacion(LocalDateTime.now()).build();

        citaCancelada = Cita.builder()
                .id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("CANCELADA")
                .fechaCreacion(LocalDateTime.now()).build();

        responseDTO = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "PENDIENTE", LocalDateTime.now());

        pacienteActivo  = new PacienteDTO(1L, "DNI", "12345678", "Juan", "Pérez", "ACTIVO");
        pacienteInactivo = new PacienteDTO(1L, "DNI", "12345678", "Juan", "Pérez", "INACTIVO");
    }

    @Test
    void programar_conPacienteActivoSinDuplicado_debeGuardarCita() {
        when(pacienteClient.obtenerPaciente(1L)).thenReturn(Mono.just(pacienteActivo));
        when(citaRepository.findByPacienteIdAndFechaHora(1L, fechaFutura)).thenReturn(Mono.empty());
        when(citaMapper.toEntity(requestDTO)).thenReturn(citaPendiente);
        when(citaRepository.save(any(Cita.class))).thenReturn(Mono.just(citaPendiente));
        when(citaMapper.toResponseDTO(citaPendiente)).thenReturn(responseDTO);

        StepVerifier.create(citaService.programar(requestDTO))
                .expectNextMatches(r -> r.estado().equals("PENDIENTE") && r.pacienteId().equals(1L))
                .verifyComplete();
    }

    @Test
    void programar_conPacienteInexistente_debeLanzarPacienteNoValidoException() {
        when(pacienteClient.obtenerPaciente(1L)).thenReturn(Mono.empty());

        StepVerifier.create(citaService.programar(requestDTO))
                .expectError(PacienteNoValidoException.class)
                .verify();
    }

    @Test
    void programar_conPacienteInactivo_debeLanzarPacienteNoValidoException() {
        when(pacienteClient.obtenerPaciente(1L)).thenReturn(Mono.just(pacienteInactivo));

        StepVerifier.create(citaService.programar(requestDTO))
                .expectError(PacienteNoValidoException.class)
                .verify();
    }

    @Test
    void programar_conCitaDuplicada_debeLanzarCitaDuplicadaException() {
        when(pacienteClient.obtenerPaciente(1L)).thenReturn(Mono.just(pacienteActivo));
        when(citaRepository.findByPacienteIdAndFechaHora(1L, fechaFutura))
                .thenReturn(Mono.just(citaPendiente));

        StepVerifier.create(citaService.programar(requestDTO))
                .expectError(CitaDuplicadaException.class)
                .verify();
    }

    @Test
    void listar_debeRetornarTodasLasCitas() {
        when(citaRepository.findAll()).thenReturn(Flux.just(citaPendiente, citaConfirmada));
        when(citaMapper.toResponseDTO(any(Cita.class))).thenReturn(responseDTO);

        StepVerifier.create(citaService.listar())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarCita() {
        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaPendiente));
        when(citaMapper.toResponseDTO(citaPendiente)).thenReturn(responseDTO);

        StepVerifier.create(citaService.obtenerPorId(10L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarCitaNotFoundException() {
        when(citaRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(citaService.obtenerPorId(99L))
                .expectError(CitaNotFoundException.class)
                .verify();
    }

    @Test
    void confirmar_citaPendiente_debeConfirmarYPublicarEvento() {
        Cita guardada = Cita.builder().id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("CONFIRMADA").fechaCreacion(LocalDateTime.now()).build();
        CitaResponseDTO confirmadaDTO = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "CONFIRMADA", LocalDateTime.now());

        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaPendiente));
        when(citaRepository.save(any(Cita.class))).thenReturn(Mono.just(guardada));
        when(citaMapper.toResponseDTO(guardada)).thenReturn(confirmadaDTO);
        when(rabbitMQProperties.getExchange()).thenReturn("hospital.eventos");
        when(rabbitMQProperties.getRoutingKeyCitaConfirmada()).thenReturn("cita.confirmada");
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), anyString(), any(CitaConfirmadaEvent.class));

        StepVerifier.create(citaService.confirmar(10L))
                .expectNextMatches(r -> r.estado().equals("CONFIRMADA"))
                .verifyComplete();

        verify(rabbitTemplate).convertAndSend(eq("hospital.eventos"), eq("cita.confirmada"), any(CitaConfirmadaEvent.class));
    }

    @Test
    void confirmar_citaNoEncontrada_debeLanzarCitaNotFoundException() {
        when(citaRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(citaService.confirmar(99L))
                .expectError(CitaNotFoundException.class)
                .verify();
    }

    @Test
    void confirmar_citaYaConfirmada_debeLanzarTransicionEstadoInvalidaException() {
        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaConfirmada));

        StepVerifier.create(citaService.confirmar(10L))
                .expectError(TransicionEstadoInvalidaException.class)
                .verify();
    }

    @Test
    void atender_citaConfirmada_debeMarcarComoAtendida() {
        Cita atendida = Cita.builder().id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("ATENDIDA").fechaCreacion(LocalDateTime.now()).build();
        CitaResponseDTO atendidaDTO = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "ATENDIDA", LocalDateTime.now());

        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaConfirmada));
        when(citaRepository.save(any(Cita.class))).thenReturn(Mono.just(atendida));
        when(citaMapper.toResponseDTO(atendida)).thenReturn(atendidaDTO);

        StepVerifier.create(citaService.atender(10L))
                .expectNextMatches(r -> r.estado().equals("ATENDIDA"))
                .verifyComplete();
    }

    @Test
    void atender_citaPendiente_debeLanzarTransicionEstadoInvalidaException() {
        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaPendiente));

        StepVerifier.create(citaService.atender(10L))
                .expectError(TransicionEstadoInvalidaException.class)
                .verify();
    }

    @Test
    void cancelar_citaPendiente_debeCancelar() {
        Cita cancelada = Cita.builder().id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("CANCELADA").fechaCreacion(LocalDateTime.now()).build();
        CitaResponseDTO canceladaDTO = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "CANCELADA", LocalDateTime.now());

        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaPendiente));
        when(citaRepository.save(any(Cita.class))).thenReturn(Mono.just(cancelada));
        when(citaMapper.toResponseDTO(cancelada)).thenReturn(canceladaDTO);

        StepVerifier.create(citaService.cancelar(10L))
                .expectNextMatches(r -> r.estado().equals("CANCELADA"))
                .verifyComplete();
    }

    @Test
    void cancelar_citaAtendida_debeLanzarTransicionEstadoInvalidaException() {
        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaAtendida));

        StepVerifier.create(citaService.cancelar(10L))
                .expectError(TransicionEstadoInvalidaException.class)
                .verify();
    }

    @Test
    void reprogramar_citaAtendida_debeLanzarTransicionEstadoInvalidaException() {
        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaAtendida));

        StepVerifier.create(citaService.reprogramar(10L, requestDTO))
                .expectError(TransicionEstadoInvalidaException.class)
                .verify();
    }

    @Test
    void reprogramar_citaCancelada_debeLanzarTransicionEstadoInvalidaException() {
        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaCancelada));

        StepVerifier.create(citaService.reprogramar(10L, requestDTO))
                .expectError(TransicionEstadoInvalidaException.class)
                .verify();
    }

    @Test
    void marcarAusente_citaAtendida_debeLanzarTransicionEstadoInvalidaException() {
        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaAtendida));

        StepVerifier.create(citaService.marcarAusente(10L))
                .expectError(TransicionEstadoInvalidaException.class)
                .verify();
    }

    @Test
    void marcarAusente_citaPendiente_debeMarcarComoAusente() {
        Cita ausente = Cita.builder().id(10L).pacienteId(1L).especialidad("Cardiología")
                .fechaHora(fechaFutura).estado("AUSENTE").fechaCreacion(LocalDateTime.now()).build();
        CitaResponseDTO ausenteDTO = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "AUSENTE", LocalDateTime.now());

        when(citaRepository.findById(10L)).thenReturn(Mono.just(citaPendiente));
        when(citaRepository.save(any(Cita.class))).thenReturn(Mono.just(ausente));
        when(citaMapper.toResponseDTO(ausente)).thenReturn(ausenteDTO);

        StepVerifier.create(citaService.marcarAusente(10L))
                .expectNextMatches(r -> r.estado().equals("AUSENTE"))
                .verifyComplete();
    }
}