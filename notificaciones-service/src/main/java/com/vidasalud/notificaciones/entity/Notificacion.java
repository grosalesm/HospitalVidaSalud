package com.vidasalud.notificaciones.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("notificaciones")
public class Notificacion {

    @Id
    private Long id;

    @Column("tipo_evento")
    private String tipoEvento;

    @Column("paciente_id")
    private Long pacienteId;

    @Column("referencia_id")
    private Long referenciaId;

    private String mensaje;

    private String estado;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
}