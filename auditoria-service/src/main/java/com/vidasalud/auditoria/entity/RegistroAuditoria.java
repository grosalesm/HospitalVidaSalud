package com.vidasalud.auditoria.entity;

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
@Table("registros_auditoria")
public class RegistroAuditoria {

    @Id
    private Long id;

    @Column("tipo_evento")
    private String tipoEvento;

    @Column("tipo_recurso")
    private String tipoRecurso;

    @Column("recurso_id")
    private Long recursoId;

    @Column("paciente_id")
    private Long pacienteId;

    @Column("estado_anterior")
    private String estadoAnterior;

    @Column("estado_nuevo")
    private String estadoNuevo;

    private String origen;

    private String descripcion;

    @Column("fecha_evento")
    private LocalDateTime fechaEvento;
}