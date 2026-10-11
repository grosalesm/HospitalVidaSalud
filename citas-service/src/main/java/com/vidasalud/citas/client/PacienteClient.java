package com.vidasalud.citas.client;

import com.vidasalud.citas.dto.PacienteDTO;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class PacienteClient {

    private final WebClient webClient;
    private final Duration timeout;

    public PacienteClient(WebClient webClient,
                          @Value("${webclient.pacientes.timeout:5000}") long timeoutMs) {
        this.webClient = webClient;
        this.timeout   = Duration.ofMillis(timeoutMs);
    }

    public Mono<PacienteDTO> obtenerPaciente(Long pacienteId) {
        return webClient.get()
                .uri("/api/pacientes/{id}", pacienteId)
                .retrieve()
                .bodyToMono(PacienteDTO.class)
                // 404 → Mono vacío; el llamador usa switchIfEmpty para lanzar PacienteNoValidoException
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                // Cualquier otro error de conectividad → Mono vacío (timeout, 5xx, etc.)
                .onErrorResume(WebClientResponseException.class, e -> Mono.empty())
                .timeout(timeout, Mono.empty());
    }
}