package com.mircroservice.matching_service.infrastructure.messaging;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mircroservice.matching_service.application.usecases.matching.MatchingRiderWithDriver;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Component
public class TripConfirmedListener {
    private final MatchingRiderWithDriver matchingUseCase;
    private final ObjectMapper objectMapper;

    public TripConfirmedListener(
            MatchingRiderWithDriver matchingUseCase,
            ObjectMapper objectMapper) {
        this.matchingUseCase = matchingUseCase;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "matching.trip.confirmed")
    public void onTripConfirmed(String payload) {
        try {
            Map<?, ?> event = objectMapper.readValue(payload, Map.class);
            String tripId = (String) event.get("tripId");
            Object fromValue = event.get("from");
            Position position;
            if (fromValue instanceof Map<?, ?> from) {
                position = new Position(
                        ((Number) from.get("latitude")).doubleValue(),
                        ((Number) from.get("longitude")).doubleValue());
            } else {
                position = new Position(0.0, 0.0);
            }
            matchingUseCase.execute(position, tripId);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not process TripConfirmed event", exception);
        }
    }
}
