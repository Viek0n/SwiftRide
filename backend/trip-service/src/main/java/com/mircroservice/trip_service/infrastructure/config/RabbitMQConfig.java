package com.mircroservice.trip_service.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TRIP_EXCHANGE = "trip.events";

    public static final String TRIP_CONFIRMED_QUEUE ="trip.confirmed.queue";

    public static final String TRIP_CONFIRMED_ROUTING_KEY = "trip.confirmed";

    @Bean
    public DirectExchange tripExchange() {
        return new DirectExchange(TRIP_EXCHANGE, true, false);
    }

    @Bean
    public Queue tripConfirmedQueue() {
        return new Queue(TRIP_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Binding tripConfirmedBinding(
            Queue tripConfirmedQueue,
            DirectExchange tripExchange) {

        return BindingBuilder
                .bind(tripConfirmedQueue)
                .to(tripExchange)
                .with(TRIP_CONFIRMED_ROUTING_KEY);
    }
}
