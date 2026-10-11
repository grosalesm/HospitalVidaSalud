package com.vidasalud.notificaciones.exception;

public class NotificacionNotFoundException extends RuntimeException {
    public NotificacionNotFoundException(String message) {
        super(message);
    }
}
