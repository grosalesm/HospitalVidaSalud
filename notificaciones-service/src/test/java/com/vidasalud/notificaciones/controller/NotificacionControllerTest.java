package com.vidasalud.notificaciones.controller;

import com.vidasalud.notificaciones.dto.NotificacionResponseDTO;
import com.vidasalud.notificaciones.exception.GlobalExceptionHandler;
import com.vidasalud.notificaciones.exception.NotificacionNotFoundException;
import com.vidasalud.notificaciones.service.NotificacionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@WebFluxTest(NotificacionController.class)
@Import(GlobalExceptionHandler.class)
class NotificacionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private NotificacionService notificacionService;

    private final LocalDateTime ahora = LocalDateTime.now();

    private NotificacionResponseDTO buildDTO(Long id, Long pacienteId) {
        return new NotificacionResponseDTO(
                id, "CITA_CONFIRMADA", pacienteId, 5L,
                "Su cita ha sido confirmada. Especialidad: Cardiología, Fecha: 2024-12-01T09:00",
                "ENVIADA", ahora
        );
    }

    @Test
    void listar_debeRetornar200ConListaDeNotificaciones() {
        when(notificacionService.listar())
                .thenReturn(Flux.just(buildDTO(1L, 10L), buildDTO(2L, 20L)));

        webTestClient.get()
                .uri("/api/notificaciones")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificacionResponseDTO.class)
                .hasSize(2);
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornar200() {
        when(notificacionService.obtenerPorId(1L))
                .thenReturn(Mono.just(buildDTO(1L, 10L)));

        webTestClient.get()
                .uri("/api/notificaciones/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(NotificacionResponseDTO.class)
                .value(dto -> {
                    assert dto.id().equals(1L);
                    assert dto.tipoEvento().equals("CITA_CONFIRMADA");
                    assert dto.estado().equals("ENVIADA");
                });
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() {
        when(notificacionService.obtenerPorId(99L))
                .thenReturn(Mono.error(new NotificacionNotFoundException("Notificación no encontrada con id: 99")));

        webTestClient.get()
                .uri("/api/notificaciones/99")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Notificación no encontrada con id: 99");
    }

    @Test
    void listarPorPaciente_debeRetornar200ConNotificacionesDelPaciente() {
        when(notificacionService.listarPorPaciente(10L))
                .thenReturn(Flux.just(buildDTO(1L, 10L)));

        webTestClient.get()
                .uri("/api/notificaciones/paciente/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificacionResponseDTO.class)
                .hasSize(1);
    }

    @Test
    void listarPorTipo_debeRetornar200FiltrandoPorTipoEvento() {
        when(notificacionService.listarPorTipoEvento("CITA_CONFIRMADA"))
                .thenReturn(Flux.just(buildDTO(1L, 10L)));

        webTestClient.get()
                .uri("/api/notificaciones/tipo/CITA_CONFIRMADA")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificacionResponseDTO.class)
                .hasSize(1);
    }
}