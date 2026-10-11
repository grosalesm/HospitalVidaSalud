package com.vidasalud.citas.entity;

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
@Table("citas")
public class Cita {

    @Id
    private Long id;

    @Column("paciente_id")
    private Long pacienteId;

    private String especialidad;

    @Column("fecha_hora")
    private LocalDateTime fechaHora;

    private String estado;

    @Column("fecha_creacion")
    private LocalDateTime fechaCreacion;
}