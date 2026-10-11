package com.vidasalud.notificaciones.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitMQProperties {
    private String exchange;
    private String queueNotificaciones;
    private String routingKeyCitaConfirmada;
}
