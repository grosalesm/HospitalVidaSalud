package com.vidasalud.pacientes.exception;

public class PacienteNotFoundException extends RuntimeException {
    public PacienteNotFoundException(String mensaje) {
        super(mensaje);
    }
}