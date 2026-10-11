package com.vidasalud.pacientes.exception;

public class PacienteDuplicadoException extends RuntimeException {
    public PacienteDuplicadoException(String mensaje) {
        super(mensaje);
    }
}