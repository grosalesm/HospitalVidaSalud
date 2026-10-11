package com.vidasalud.citas.exception;

public class CitaNotFoundException extends RuntimeException {
    public CitaNotFoundException(String mensaje) {
        super(mensaje);
    }
}