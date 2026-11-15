package com.mircroservice.payment_service.infrastructure.messaging;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mircroservice.payment_service.application.dto.PaymentSettledEvent;
import com.mircroservice.payment_service.domain.entities.Bill;
import com.mircroservice.payment_service.domain.entities.PaymentOutboxEvent;
import com.mircroservice.payment_service.domain.repositories.BillRepository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(name = "swift-ride.messaging.rabbitmq.enabled", havingValue = "true")
public class PaymentSettledOutboxPublisher {
    private final BillRepository bills;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public PaymentSettledOutboxPublisher(
            BillRepository bills,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper) {
        this.bills = bills;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${swift-ride.messaging.outbox-poll-ms:1000}")
    public void publishPendingEvents() {
        for (Bill bill : bills.findTop50ByOutboxEvents_PublishedAtIsNull()) {
            if (bill.getOutboxEvents() == null) {
                continue;
            }
            for (PaymentOutboxEvent event : bill.getOutboxEvents()) {
                if (event.getPublishedAt() == null) {
                    publish(bill, event);
                }
            }
        }
    }

    private void publish(Bill bill, PaymentOutboxEvent event) {
        PaymentSettledEvent payload = new PaymentSettledEvent(
                event.getEventId(),
                event.getEventType(),
                event.getTripId(),
                event.getRiderId(),
                event.getAmount(),
                event.getCurrency(),
                event.getGatewayTransactionId(),
                event.getOccurredAt());
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Could not serialize PaymentSettled event " + event.getEventId(), ex);
        }

        CorrelationData correlation = new CorrelationData(event.getEventId());
        rabbitTemplate.convertAndSend(
                RabbitMessagingConfiguration.EVENTS_EXCHANGE,
                RabbitMessagingConfiguration.PAYMENT_SETTLED_ROUTING_KEY,
                json,
                correlation);
        try {
            CorrelationData.Confirm confirm = correlation.getFuture().get(5, TimeUnit.SECONDS);
            if (!confirm.ack()) {
                throw new IllegalStateException("RabbitMQ rejected PaymentSettled event " + event.getEventId());
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while publishing PaymentSettled event", ex);
        } catch (java.util.concurrent.ExecutionException | java.util.concurrent.TimeoutException ex) {
            throw new IllegalStateException("Could not confirm PaymentSettled event " + event.getEventId(), ex);
        }

        event.setPublishedAt(Instant.now());
        bill.setUpdatedAt(Instant.now());
        bills.save(bill);
    }
}
