package com.vidasalud.notificaciones.client;

import com.vidasalud.notificaciones.dto.PacienteDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class PacienteClient {

    private final WebClient webClient;

    public Mono<PacienteDTO> obtenerPaciente(Long pacienteId) {
        return webClient.get()
                .uri("/api/pacientes/{id}", pacienteId)
                .retrieve()
                .bodyToMono(PacienteDTO.class)
                .onErrorResume(e -> Mono.empty());
    }
}