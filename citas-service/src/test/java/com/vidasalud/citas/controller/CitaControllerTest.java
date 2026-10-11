package com.vidasalud.citas.controller;

import com.vidasalud.citas.dto.CitaRequestDTO;
import com.vidasalud.citas.dto.CitaResponseDTO;
import com.vidasalud.citas.exception.CitaNotFoundException;
import com.vidasalud.citas.exception.CitaDuplicadaException;
import com.vidasalud.citas.exception.GlobalExceptionHandler;
import com.vidasalud.citas.exception.PacienteNoValidoException;
import com.vidasalud.citas.exception.TransicionEstadoInvalidaException;
import com.vidasalud.citas.service.CitaService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(CitaController.class)
@Import(GlobalExceptionHandler.class)
class CitaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CitaService citaService;

    private final LocalDateTime fechaFutura = LocalDateTime.now().plusDays(10);
    private CitaRequestDTO requestValido;
    private CitaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestValido = new CitaRequestDTO(1L, "Cardiología", fechaFutura);
        responseDTO   = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "PENDIENTE", LocalDateTime.now());
    }

    @Test
    void programar_conDatosValidos_debeRetornar201() {
        when(citaService.programar(any(CitaRequestDTO.class))).thenReturn(Mono.just(responseDTO));

        webTestClient.post()
                .uri("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CitaResponseDTO.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo(10L);
                    assertThat(r.estado()).isEqualTo("PENDIENTE");
                });
    }

    @Test
    void programar_conPacienteInexistente_debeRetornar404() {
        when(citaService.programar(any(CitaRequestDTO.class)))
                .thenReturn(Mono.error(new PacienteNoValidoException("El paciente con id 1 no existe")));

        webTestClient.post()
                .uri("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("El paciente con id 1 no existe");
    }

    @Test
    void programar_conCitaDuplicada_debeRetornar409() {
        when(citaService.programar(any(CitaRequestDTO.class)))
                .thenReturn(Mono.error(new CitaDuplicadaException(
                        "Ya existe una cita para ese paciente en la misma fecha y hora")));

        webTestClient.post()
                .uri("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Ya existe una cita para ese paciente en la misma fecha y hora");
    }

    @Test
    void programar_sinEspecialidad_debeRetornar400() {
        CitaRequestDTO sinEspecialidad = new CitaRequestDTO(1L, "", fechaFutura);

        webTestClient.post()
                .uri("/api/citas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(sinEspecialidad)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void listar_debeRetornar200ConTodasLasCitas() {
        CitaResponseDTO r2 = new CitaResponseDTO(11L, 2L, "Neurología", fechaFutura, "CONFIRMADA", LocalDateTime.now());
        when(citaService.listar()).thenReturn(Flux.just(responseDTO, r2));

        webTestClient.get()
                .uri("/api/citas")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CitaResponseDTO.class)
                .hasSize(2);
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornar200() {
        when(citaService.obtenerPorId(10L)).thenReturn(Mono.just(responseDTO));

        webTestClient.get()
                .uri("/api/citas/10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CitaResponseDTO.class)
                .value(r -> assertThat(r.id()).isEqualTo(10L));
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() {
        when(citaService.obtenerPorId(99L))
                .thenReturn(Mono.error(new CitaNotFoundException("Cita no encontrada con id: 99")));

        webTestClient.get()
                .uri("/api/citas/99")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Cita no encontrada con id: 99");
    }

    @Test
    void listarPorPaciente_debeRetornar200ConCitasDelPaciente() {
        when(citaService.listarPorPaciente(1L)).thenReturn(Flux.just(responseDTO));

        webTestClient.get()
                .uri("/api/citas/paciente/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CitaResponseDTO.class)
                .hasSize(1);
    }

    @Test
    void confirmar_citaPendiente_debeRetornar200() {
        CitaResponseDTO confirmada = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "CONFIRMADA", LocalDateTime.now());
        when(citaService.confirmar(10L)).thenReturn(Mono.just(confirmada));

        webTestClient.patch()
                .uri("/api/citas/10/confirmar")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CitaResponseDTO.class)
                .value(r -> assertThat(r.estado()).isEqualTo("CONFIRMADA"));
    }

    @Test
    void confirmar_transicionInvalida_debeRetornar409() {
        when(citaService.confirmar(10L))
                .thenReturn(Mono.error(new TransicionEstadoInvalidaException(
                        "Solo se puede confirmar una cita PENDIENTE. Estado actual: CONFIRMADA")));

        webTestClient.patch()
                .uri("/api/citas/10/confirmar")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.mensaje").value(msg -> assertThat(msg.toString()).contains("PENDIENTE"));
    }

    @Test
    void atender_citaConfirmada_debeRetornar200() {
        CitaResponseDTO atendida = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "ATENDIDA", LocalDateTime.now());
        when(citaService.atender(10L)).thenReturn(Mono.just(atendida));

        webTestClient.patch()
                .uri("/api/citas/10/atender")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CitaResponseDTO.class)
                .value(r -> assertThat(r.estado()).isEqualTo("ATENDIDA"));
    }

    @Test
    void cancelar_citaValida_debeRetornar200() {
        CitaResponseDTO cancelada = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "CANCELADA", LocalDateTime.now());
        when(citaService.cancelar(10L)).thenReturn(Mono.just(cancelada));

        webTestClient.patch()
                .uri("/api/citas/10/cancelar")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CitaResponseDTO.class)
                .value(r -> assertThat(r.estado()).isEqualTo("CANCELADA"));
    }

    @Test
    void marcarAusente_citaValida_debeRetornar200() {
        CitaResponseDTO ausente = new CitaResponseDTO(10L, 1L, "Cardiología", fechaFutura, "AUSENTE", LocalDateTime.now());
        when(citaService.marcarAusente(10L)).thenReturn(Mono.just(ausente));

        webTestClient.patch()
                .uri("/api/citas/10/ausente")
                .exchange()
                .expectStatus().isOk()
                .expectBody(CitaResponseDTO.class)
                .value(r -> assertThat(r.estado()).isEqualTo("AUSENTE"));
    }

    @Test
    void reprogramar_citaValida_debeRetornar200() {
        LocalDateTime nuevaFecha = fechaFutura.plusDays(5);
        CitaRequestDTO reprogramarDTO = new CitaRequestDTO(1L, "Neurología", nuevaFecha);
        CitaResponseDTO reprogramada  = new CitaResponseDTO(10L, 1L, "Neurología", nuevaFecha, "PENDIENTE", LocalDateTime.now());

        when(citaService.reprogramar(eq(10L), any(CitaRequestDTO.class))).thenReturn(Mono.just(reprogramada));

        webTestClient.put()
                .uri("/api/citas/10/reprogramar")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(reprogramarDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CitaResponseDTO.class)
                .value(r -> assertThat(r.especialidad()).isEqualTo("Neurología"));
    }
}
