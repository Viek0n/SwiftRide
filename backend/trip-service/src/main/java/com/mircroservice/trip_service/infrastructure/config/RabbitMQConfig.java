package com.mircroservice.trip_service.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String TRIP_EXCHANGE = "trip.exchange";
    public static final String MATCHING_EXCHANGE = "matching.exchange";
    public static final String TRIP_MATCHING_FAILED_QUEUE = "trip.matching.failed";
    public static final String TRIP_DRIVER_ACCEPTED_QUEUE = "trip.driver.accepted";

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

    @Bean
    public DirectExchange matchingExchange() {
        return new DirectExchange(MATCHING_EXCHANGE, true, false);
    }

    @Bean
    public Queue tripMatchingFailedQueue() {
        return new Queue(TRIP_MATCHING_FAILED_QUEUE, true);
    }

    @Bean
    public Binding tripMatchingFailedBinding(Queue tripMatchingFailedQueue, DirectExchange matchingExchange) {
        return BindingBuilder.bind(tripMatchingFailedQueue).to(matchingExchange).with("matching.failed");
    }

    @Bean
    public Queue tripDriverAcceptedQueue() {
        return new Queue(TRIP_DRIVER_ACCEPTED_QUEUE, true);
    }

    @Bean
    public Binding tripDriverAcceptedBinding(Queue tripDriverAcceptedQueue, DirectExchange matchingExchange) {
        return BindingBuilder.bind(tripDriverAcceptedQueue).to(matchingExchange).with("driver.accepted");
    }
}
