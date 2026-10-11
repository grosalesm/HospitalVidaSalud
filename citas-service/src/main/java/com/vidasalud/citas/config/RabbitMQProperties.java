package com.vidasalud.citas.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitMQProperties {
    private String exchange;
    private String queueNotificaciones;
    private String queueAuditoria;
    private String routingKeyCitaConfirmada;
    private String routingKeyCitaCancelada;
    private String routingKeyCitaReprogramada;
}