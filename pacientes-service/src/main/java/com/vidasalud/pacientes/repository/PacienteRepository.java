package com.vidasalud.pacientes.repository;

import com.vidasalud.pacientes.entity.Paciente;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PacienteRepository extends ReactiveCrudRepository<Paciente, Long> {

    Mono<Paciente> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);
    Flux<Paciente> findByApellidosContainingIgnoreCase(String apellidos);
    @Query("SELECT * FROM pacientes WHERE estado = :estado")
    Flux<Paciente> findByEstado(String estado);
}