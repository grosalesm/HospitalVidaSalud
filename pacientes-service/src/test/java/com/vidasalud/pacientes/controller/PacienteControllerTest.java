package com.vidasalud.pacientes.controller;

import com.vidasalud.pacientes.dto.PacienteRequestDTO;
import com.vidasalud.pacientes.dto.PacienteResponseDTO;
import com.vidasalud.pacientes.exception.GlobalExceptionHandler;
import com.vidasalud.pacientes.exception.PacienteDuplicadoException;
import com.vidasalud.pacientes.exception.PacienteNotFoundException;
import com.vidasalud.pacientes.service.PacienteService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(PacienteController.class)
@Import(GlobalExceptionHandler.class)
class PacienteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PacienteService pacienteService;

    @MockBean
    private com.vidasalud.pacientes.repository.PacienteRepository pacienteRepository;

    private PacienteRequestDTO requestValido;
    private PacienteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestValido = new PacienteRequestDTO(
                "DNI", "12345678", "Juan", "Pérez",
                LocalDate.of(1990, 5, 15), "987654321",
                "juan@example.com", "Av. Lima 123"
        );

        responseDTO = new PacienteResponseDTO(
                1L, "DNI", "12345678", "Juan", "Pérez",
                LocalDate.of(1990, 5, 15), "987654321",
                "juan@example.com", "Av. Lima 123",
                LocalDateTime.now(), "ACTIVO"
        );
    }

    @Test
    void registrar_conDatosValidos_debeRetornar201() {
        when(pacienteService.registrar(any(PacienteRequestDTO.class)))
                .thenReturn(Mono.just(responseDTO));

        webTestClient.post()
                .uri("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(PacienteResponseDTO.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo(1L);
                    assertThat(r.estado()).isEqualTo("ACTIVO");
                });
    }

    @Test
    void registrar_conDocumentoDuplicado_debeRetornar409() {
        when(pacienteService.registrar(any(PacienteRequestDTO.class)))
                .thenReturn(Mono.error(new PacienteDuplicadoException("Ya existe un paciente con ese documento")));

        webTestClient.post()
                .uri("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Ya existe un paciente con ese documento");
    }

    @Test
    void registrar_conTipoDocumentoInvalido_debeRetornar400() {
        PacienteRequestDTO requestInvalido = new PacienteRequestDTO(
                "LIBRETA", "12345678", "Juan", "Pérez",
                LocalDate.of(1990, 5, 15), "987654321",
                "juan@example.com", null
        );

        webTestClient.post()
                .uri("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestInvalido)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void registrar_sinNombres_debeRetornar400() {
        PacienteRequestDTO requestInvalido = new PacienteRequestDTO(
                "DNI", "12345678", "", "Pérez",
                LocalDate.of(1990, 5, 15), null, null, null
        );

        webTestClient.post()
                .uri("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestInvalido)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void listar_debeRetornar200ConListaDePacientes() {
        PacienteResponseDTO r2 = new PacienteResponseDTO(
                2L, "CE", "87654321", "Ana", "García",
                LocalDate.of(1985, 3, 20), null, null, null, LocalDateTime.now(), "ACTIVO"
        );

        when(pacienteService.listar()).thenReturn(Flux.just(responseDTO, r2));

        webTestClient.get()
                .uri("/api/pacientes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PacienteResponseDTO.class)
                .hasSize(2);
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornar200() {
        when(pacienteService.obtenerPorId(1L)).thenReturn(Mono.just(responseDTO));

        webTestClient.get()
                .uri("/api/pacientes/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PacienteResponseDTO.class)
                .value(r -> {
                    assertThat(r.id()).isEqualTo(1L);
                    assertThat(r.nombres()).isEqualTo("Juan");
                });
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeRetornar404() {
        when(pacienteService.obtenerPorId(99L))
                .thenReturn(Mono.error(new PacienteNotFoundException("Paciente no encontrado con id: 99")));

        webTestClient.get()
                .uri("/api/pacientes/99")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.mensaje").isEqualTo("Paciente no encontrado con id: 99");
    }

    @Test
    void obtenerPorDocumento_cuandoExiste_debeRetornar200() {
        when(pacienteService.obtenerPorDocumento("DNI", "12345678"))
                .thenReturn(Mono.just(responseDTO));

        webTestClient.get()
                .uri("/api/pacientes/documento/DNI/12345678")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PacienteResponseDTO.class)
                .value(r -> assertThat(r.numeroDocumento()).isEqualTo("12345678"));
    }

    @Test
    void actualizar_cuandoExiste_debeRetornar200() {
        when(pacienteService.actualizar(eq(1L), any(PacienteRequestDTO.class)))
                .thenReturn(Mono.just(responseDTO));

        webTestClient.put()
                .uri("/api/pacientes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestValido)
                .exchange()
                .expectStatus().isOk()
                .expectBody(PacienteResponseDTO.class)
                .value(r -> assertThat(r.id()).isEqualTo(1L));
    }

    @Test
    void cambiarEstado_debeRetornar200() {
        PacienteResponseDTO inactivo = new PacienteResponseDTO(
                1L, "DNI", "12345678", "Juan", "Pérez",
                LocalDate.of(1990, 5, 15), "987654321",
                "juan@example.com", "Av. Lima 123",
                LocalDateTime.now(), "INACTIVO"
        );

        when(pacienteService.cambiarEstado(1L, "INACTIVO")).thenReturn(Mono.just(inactivo));

        webTestClient.patch()
                .uri("/api/pacientes/1/estado?estado=INACTIVO")
                .exchange()
                .expectStatus().isOk()
                .expectBody(PacienteResponseDTO.class)
                .value(r -> assertThat(r.estado()).isEqualTo("INACTIVO"));
    }
}