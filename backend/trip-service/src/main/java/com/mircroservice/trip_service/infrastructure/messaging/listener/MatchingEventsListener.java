package com.mircroservice.trip_service.infrastructure.messaging.listener;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mircroservice.trip_service.infrastructure.services.TripService;

@Component
public class MatchingEventsListener {
    private final TripService tripService;
    private final ObjectMapper objectMapper;

    public MatchingEventsListener(TripService tripService, ObjectMapper objectMapper) {
        this.tripService = tripService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "trip.matching.failed")
    public void onMatchingFailed(String payload) {
        try {
            Map<?, ?> event = objectMapper.readValue(payload, Map.class);
            tripService.markMatchingFailed((String) event.get("tripId"));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not process MatchingFailed event", exception);
        }
    }

    @RabbitListener(queues = "trip.driver.accepted")
    public void onDriverAccepted(String payload) {
        try {
            Map<?, ?> event = objectMapper.readValue(payload, Map.class);
            tripService.assignAcceptedDriver(
                    (String) event.get("tripId"),
                    (String) event.get("driverId"));
        } catch (Exception exception) {
            throw new IllegalStateException("Could not process DriverAccepted event", exception);
        }
    }
}
