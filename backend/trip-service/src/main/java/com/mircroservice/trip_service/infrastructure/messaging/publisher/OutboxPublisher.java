package com.mircroservice.trip_service.infrastructure.messaging.publisher;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mircroservice.trip_service.domain.repositories.OutboxRepository;
import com.mircroservice.trip_service.infrastructure.config.RabbitMQConfig;
import com.mircroservice.trip_service.infrastructure.documents.OutboxDocument;
import com.mircroservice.trip_service.infrastructure.repositories.OutboxMongoRepository;

import java.time.Instant;
import java.util.UUID;

@Component
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final MongoTemplate mongoTemplate;

    public OutboxPublisher(
            OutboxMongoRepository outboxRepository,
            RabbitTemplate rabbitTemplate,
            ObjectMapper objectMapper,
            MongoTemplate mongoTemplate) {

        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(fixedDelay = 1000)
    public void publishPendingEvents() {

        List<OutboxDocument> events =
                outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(
                        "PENDING"
                );

        for (OutboxDocument event : events) {
            try {
                publish(event);
            } catch (Exception exception) {
                System.err.println(
                        "Failed to publish event: " + event.getId()
                );

                System.err.println(exception.getMessage());

                event.setRetryCount(
                        event.getRetryCount() == null
                                ? 1
                                : event.getRetryCount() + 1
                );

                // Leave it PENDING so it can be retried.
                outboxRepository.save(event);
            }
        }
    }

    private void publish(OutboxDocument event) throws Exception {

        String routingKey = getRoutingKey(event.getEventType());

        String json = objectMapper.writeValueAsString(event.getPayload());

        CorrelationData correlationData =
                new CorrelationData(UUID.randomUUID().toString());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TRIP_EXCHANGE,
                routingKey,
                json,
                correlationData
        );

        CorrelationData.Confirm confirm = correlationData
                .getFuture()
                .get(10, TimeUnit.SECONDS);

        if (!confirm.isAck()) {
            throw new IllegalStateException(
                    "RabbitMQ did not confirm message: "
                            + confirm.getReason()
            );
        }

        // Mark as published only after broker confirmation.
        Query query = new Query(
                Criteria.where("_id").is(event.getId())
                        .and("status").is("PENDING")
        );

        Update update = new Update()
                .set("status", "PUBLISHED")
                .set("publishedAt", Instant.now());

        mongoTemplate.updateFirst(
                query,
                update,
                OutboxDocument.class
        );
    }

    private String getRoutingKey(String eventType) {

        return switch (eventType) {
            case "TripConfirmed" -> "trip.confirmed";
            default -> throw new IllegalArgumentException(
                    "Unknown event type: " + eventType
            );
        };
    }
}