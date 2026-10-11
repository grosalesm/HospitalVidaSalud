package com.vidasalud.auditoria.controller;

import com.vidasalud.auditoria.dto.AuditoriaResponseDTO;
import com.vidasalud.auditoria.exception.AuditoriaNotFoundException;
import com.vidasalud.auditoria.exception.GlobalExceptionHandler;
import com.vidasalud.auditoria.service.AuditoriaService;
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

@WebFluxTest(AuditoriaController.class)
@Import(GlobalExceptionHandler.class)
class AuditoriaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AuditoriaService auditoriaService;

    private final LocalDateTime ahora = LocalDateTime.now();

    private AuditoriaResponseDTO buildDTO(Long id, Long recursoId, Long pacienteId) {
        return new AuditoriaResponseDTO(
                id, "CITA_CONFIRMADA", "CITA", recursoId, pacienteId,
                "PENDIENTE", "CONFIRMADA", "citas-service",
                "Cita " + recursoId + " confirmada para paciente " + pacienteId,
                ahora
        );
    }

    @Test
    void listar_debeRetornar200ConTodosLosRegistros() {
        when(auditoriaService.listar())
                .thenReturn(Flux.just(buildDTO(1L, 5L, 10L), buildDTO(2L, 6L, 11L)));

        webTestClient.get()
                .uri("/api/auditoria")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuditoriaResponseDTO.class)
                .hasSize(2);
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornar200() {
        when(auditoriaService.obtenerPorId(1L))
                .thenReturn(Mono.just(buildDTO(1L, 5L, 10L)));

        webTestClient.get()
                .uri("/api/auditoria/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuditoriaResponseDTO.class)
                .value(dto -> {
                    assert dto.id().equals(1L);
                    assert dto.estadoAnterior().equals("PENDIENTE");
                    assert dto.estadoNuevo().equals("CONFIRMADA");
                    assert dto.origen().equals("citas-service");
                });
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() {
        when(auditoriaService.obtenerPorId(99L))
                .thenReturn(Mono.error(
                        new AuditoriaNotFoundException("Registro de auditoría no encontrado con id: 99")));

        webTestClient.get()
                .uri("/api/auditoria/99")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Registro de auditoría no encontrado con id: 99");
    }

    @Test
    void listarPorRecurso_debeRetornar200ConRegistrosDeLaCita() {
        when(auditoriaService.listarPorRecurso("CITA", 5L))
                .thenReturn(Flux.just(buildDTO(1L, 5L, 10L)));

        webTestClient.get()
                .uri("/api/auditoria/recurso/CITA/5")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuditoriaResponseDTO.class)
                .hasSize(1);
    }

    @Test
    void listarPorPaciente_debeRetornar200ConRegistrosDelPaciente() {
        when(auditoriaService.listarPorPaciente(10L))
                .thenReturn(Flux.just(buildDTO(1L, 5L, 10L)));

        webTestClient.get()
                .uri("/api/auditoria/paciente/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuditoriaResponseDTO.class)
                .hasSize(1);
    }

    @Test
    void listarPorTipoEvento_debeRetornar200FiltradoPorEvento() {
        when(auditoriaService.listarPorTipoEvento("CITA_CONFIRMADA"))
                .thenReturn(Flux.just(buildDTO(1L, 5L, 10L)));

        webTestClient.get()
                .uri("/api/auditoria/evento/CITA_CONFIRMADA")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AuditoriaResponseDTO.class)
                .hasSize(1);
    }
}