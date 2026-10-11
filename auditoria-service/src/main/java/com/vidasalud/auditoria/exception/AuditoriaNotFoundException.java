package com.vidasalud.auditoria.exception;

public class AuditoriaNotFoundException extends RuntimeException {
    public AuditoriaNotFoundException(String message) {
        super(message);
    }
}