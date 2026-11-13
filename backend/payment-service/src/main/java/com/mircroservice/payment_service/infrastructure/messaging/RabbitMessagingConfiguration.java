package com.mircroservice.payment_service.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "swift-ride.messaging.rabbitmq.enabled", havingValue = "true")
public class RabbitMessagingConfiguration {
    public static final String EVENTS_EXCHANGE = "swiftride.events";
    public static final String DEAD_LETTER_EXCHANGE = "swiftride.events.dlx";
    public static final String RIDE_COMPLETED_ROUTING_KEY = "trip.ride-completed";
    public static final String RIDE_COMPLETED_FAILED_ROUTING_KEY = "trip.ride-completed.failed";
    public static final String PAYMENT_SETTLED_ROUTING_KEY = "payment.settled";

    @Bean
    TopicExchange swiftRideEventsExchange() {
        return new TopicExchange(EVENTS_EXCHANGE, true, false);
    }

    @Bean
    TopicExchange swiftRideDeadLetterExchange() {
        return new TopicExchange(DEAD_LETTER_EXCHANGE, true, false);
    }

    @Bean
    Queue rideCompletedQueue(
            @Value("${swift-ride.messaging.ride-completed-queue}") String queueName) {
        return new Queue(queueName, true, false, false,
                java.util.Map.of(
                        "x-dead-letter-exchange", DEAD_LETTER_EXCHANGE,
                        "x-dead-letter-routing-key", RIDE_COMPLETED_FAILED_ROUTING_KEY));
    }

    @Bean
    Queue rideCompletedDeadLetterQueue(
            @Value("${swift-ride.messaging.ride-completed-queue}") String queueName) {
        return new Queue(queueName + ".dlq", true);
    }

    @Bean
    Binding rideCompletedBinding(
            @Qualifier("rideCompletedQueue") Queue rideCompletedQueue,
            @Qualifier("swiftRideEventsExchange") TopicExchange swiftRideEventsExchange) {
        return BindingBuilder.bind(rideCompletedQueue)
                .to(swiftRideEventsExchange)
                .with(RIDE_COMPLETED_ROUTING_KEY);
    }

    @Bean
    Binding rideCompletedDeadLetterBinding(
            @Qualifier("rideCompletedDeadLetterQueue") Queue rideCompletedDeadLetterQueue,
            @Qualifier("swiftRideDeadLetterExchange") TopicExchange swiftRideDeadLetterExchange) {
        return BindingBuilder.bind(rideCompletedDeadLetterQueue)
                .to(swiftRideDeadLetterExchange)
                .with(RIDE_COMPLETED_FAILED_ROUTING_KEY);
    }
}
