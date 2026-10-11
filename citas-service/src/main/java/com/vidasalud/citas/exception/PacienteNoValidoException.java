package com.vidasalud.citas.exception;

public class PacienteNoValidoException extends RuntimeException {
    public PacienteNoValidoException(String mensaje) {
        super(mensaje);
    }
}