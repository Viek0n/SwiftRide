package com.mircroservice.matching_service.infrastructure.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.DirectExchange;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String EXCHANGE = "swiftride.events";
    public static final String TRIP_EXCHANGE = "trip.exchange";
    public static final String TRIP_CONFIRMED_QUEUE = "matching.trip.confirmed";

    public static final String MATCHING_QUEUE =
            "matching.trip.requested";

    @Bean
    public TopicExchange swiftrideExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue matchingQueue() {
        return new Queue(MATCHING_QUEUE, true);
    }

    @Bean
    public Binding matchingBinding(
        Queue matchingQueue,
        TopicExchange swiftrideExchange
    ) {

        return BindingBuilder
                .bind(matchingQueue)
                .to(swiftrideExchange)
                .with("trip.requested");
    }

    @Bean
    public DirectExchange tripExchange() {
        return new DirectExchange(TRIP_EXCHANGE, true, false);
    }

    @Bean
    public Queue tripConfirmedQueue() {
        return new Queue(TRIP_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Binding tripConfirmedBinding(Queue tripConfirmedQueue, DirectExchange tripExchange) {
        return BindingBuilder.bind(tripConfirmedQueue).to(tripExchange).with("trip.confirmed");
    }
}