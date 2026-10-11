package com.vidasalud.pacientes.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("pacientes")
public class Paciente {

    @Id
    private Long id;

    @Column("tipo_documento")
    private String tipoDocumento;

    @Column("numero_documento")
    private String numeroDocumento;

    private String nombres;

    private String apellidos;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    private String telefono;

    private String correo;

    private String direccion;

    @Column("fecha_registro")
    private LocalDateTime fechaRegistro;

    private String estado;
}