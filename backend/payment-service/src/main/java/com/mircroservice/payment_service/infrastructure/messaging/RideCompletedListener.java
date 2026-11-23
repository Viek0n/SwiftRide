package com.mircroservice.payment_service.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.mircroservice.payment_service.application.dto.RideCompletedEvent;
import com.mircroservice.payment_service.application.use_cases.ProcessPayment;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(name = "swift-ride.messaging.rabbitmq.enabled", havingValue = "true")
public class RideCompletedListener {
    private final ObjectMapper objectMapper;
    private final ProcessPayment paymentService;

    public RideCompletedListener(ObjectMapper objectMapper, ProcessPayment paymentService) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
    }

    @RabbitListener(queues = "${swift-ride.messaging.ride-completed-queue}")
    public void onRideCompleted(String payload) {
        RideCompletedEvent event;
        try {
            event = objectMapper.readValue(payload, RideCompletedEvent.class);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Could not process RideCompleted event", ex);
        }
        paymentService.registerCompletedRide(event);
    }
}
