package com.vidasalud.citas.config;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RabbitMQProperties.class)
public class RabbitMQConfig {

    private final RabbitMQProperties props;

    public RabbitMQConfig(RabbitMQProperties props) {
        this.props = props;
    }

    @PostConstruct
    public void init() {
        System.out.println(">>> RabbitMQConfig cargado correctamente");
        System.out.println(">>> Exchange: " + props.getExchange());
        System.out.println(">>> Queue notificaciones: " + props.getQueueNotificaciones());
        System.out.println(">>> Queue auditoria: " + props.getQueueAuditoria());
        System.out.println(">>> Routing key: " + props.getRoutingKeyCitaConfirmada());
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        return admin;
    }

    @Bean
    public TopicExchange hospitalExchange() {
        return new TopicExchange(props.getExchange());
    }

    @Bean
    public Queue notificacionesQueue() {
        return new Queue(props.getQueueNotificaciones(), true);
    }

    @Bean
    public Queue auditoriaQueue() {
        return new Queue(props.getQueueAuditoria(), true);
    }

    @Bean
    public Binding notificacionesBinding(Queue notificacionesQueue, TopicExchange hospitalExchange) {
        return BindingBuilder.bind(notificacionesQueue)
                .to(hospitalExchange)
                .with(props.getRoutingKeyCitaConfirmada());
    }

    @Bean
    public Binding auditoriaBinding(Queue auditoriaQueue, TopicExchange hospitalExchange) {
        return BindingBuilder.bind(auditoriaQueue)
                .to(hospitalExchange)
                .with(props.getRoutingKeyCitaConfirmada());
    }
}