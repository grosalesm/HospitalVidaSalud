package com.vidasalud.notificaciones.service;

import com.vidasalud.notificaciones.dto.NotificacionResponseDTO;
import com.vidasalud.notificaciones.entity.Notificacion;
import com.vidasalud.notificaciones.exception.NotificacionNotFoundException;
import com.vidasalud.notificaciones.mapper.NotificacionMapper;
import com.vidasalud.notificaciones.repository.NotificacionRepository;
import com.vidasalud.notificaciones.service.impl.NotificacionServiceImpl;
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
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private NotificacionMapper notificacionMapper;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private Notificacion notificacion;
    private NotificacionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        notificacion = Notificacion.builder()
                .id(1L)
                .tipoEvento("CITA_CONFIRMADA")
                .pacienteId(10L)
                .referenciaId(5L)
                .mensaje("Su cita ha sido confirmada. Especialidad: Cardiología, Fecha: 2024-12-01T09:00")
                .estado("ENVIADA")
                .fechaCreacion(LocalDateTime.now())
                .build();

        responseDTO = new NotificacionResponseDTO(
                1L, "CITA_CONFIRMADA", 10L, 5L,
                "Su cita ha sido confirmada. Especialidad: Cardiología, Fecha: 2024-12-01T09:00",
                "ENVIADA", notificacion.getFechaCreacion()
        );
    }

    @Test
    void guardar_debeGuardarNotificacionCorrectamente() {
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(Mono.just(notificacion));

        StepVerifier.create(notificacionService.guardar(notificacion))
                .expectNextMatches(n -> n.getId().equals(1L)
                        && n.getTipoEvento().equals("CITA_CONFIRMADA")
                        && n.getEstado().equals("ENVIADA"))
                .verifyComplete();
    }

    @Test
    void listar_debeRetornarTodasLasNotificaciones() {
        when(notificacionRepository.findAll()).thenReturn(Flux.just(notificacion));
        when(notificacionMapper.toResponseDTO(notificacion)).thenReturn(responseDTO);

        StepVerifier.create(notificacionService.listar())
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarNotificacion() {
        when(notificacionRepository.findById(1L)).thenReturn(Mono.just(notificacion));
        when(notificacionMapper.toResponseDTO(notificacion)).thenReturn(responseDTO);

        StepVerifier.create(notificacionService.obtenerPorId(1L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarNotificacionNotFoundException() {
        when(notificacionRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(notificacionService.obtenerPorId(99L))
                .expectError(NotificacionNotFoundException.class)
                .verify();
    }

    @Test
    void listarPorPaciente_debeRetornarNotificacionesDelPaciente() {
        when(notificacionRepository.findByPacienteId(10L)).thenReturn(Flux.just(notificacion));
        when(notificacionMapper.toResponseDTO(notificacion)).thenReturn(responseDTO);

        StepVerifier.create(notificacionService.listarPorPaciente(10L))
                .expectNext(responseDTO)
                .verifyComplete();
    }

    @Test
    void listarPorTipoEvento_debeRetornarNotificacionesFiltradas() {
        when(notificacionRepository.findByTipoEvento("CITA_CONFIRMADA")).thenReturn(Flux.just(notificacion));
        when(notificacionMapper.toResponseDTO(notificacion)).thenReturn(responseDTO);

        StepVerifier.create(notificacionService.listarPorTipoEvento("CITA_CONFIRMADA"))
                .expectNext(responseDTO)
                .verifyComplete();
    }
}