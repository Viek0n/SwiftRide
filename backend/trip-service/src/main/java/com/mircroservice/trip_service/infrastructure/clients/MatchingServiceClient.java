package com.mircroservice.trip_service.infrastructure.clients;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.mircroservice.trip_service.domain.value_objects.Position;

@Component
public class MatchingServiceClient {
    private final RestClient restClient;

    public MatchingServiceClient(
        @Qualifier("matchingServiceRestClientBuilder")
        RestClient.Builder builder,

        @Value("${matching.service.base-url:http://matching-service}")
        String baseUrl
    ) {
        this.restClient = builder.clone()
            .baseUrl(baseUrl)
            .build();
    }


    public RouteResult getRoute(Position origin, Position destination) {
        RouteResult route = restClient.post()
            .uri("/api/match/route")
            .body(new RouteRequest(origin, destination))
            .retrieve()
            .body(RouteResult.class);
        if (route == null) {
            throw new IllegalStateException("Matching service returned an empty route");
        }
        return route;
    }

    public FareResult calculateFare(Position origin, Position destination) {
        FareResult fare = restClient.post()
            .uri("/api/match/quote")
            .body(new FareRequest(origin, destination, false, false))
            .retrieve()
            .body(FareResult.class);
        if (fare == null || fare.totalFare() == null) {
            throw new IllegalStateException("Matching service returned an empty fare");
        }
        return fare;
    }

    public Optional<MatchResult> match(String tripId, String userId, Position origin) {
        ResponseEntity<MatchResult> response = restClient.post()
            .uri("/api/match")
            .body(new MatchRequest(tripId, userId, origin))
            .retrieve()
            .toEntity(MatchResult.class);
        return Optional.ofNullable(response.getBody());
    }

    public void accept(String tripId, String driverId) {
        decide("accept", tripId, driverId);
    }

    public void decline(String tripId, String driverId) {
        decide("decline", tripId, driverId);
    }

    public void confirm(String tripId, String driverId) {
        decide("confirm", tripId, driverId);
    }

    private void decide(String path, String tripId, String driverId) {
        restClient.post()
            .uri("/api/match/" + path)
            .body(new MatchDecisionRequest(tripId, driverId))
            .retrieve()
            .toBodilessEntity();
    }

    public record RouteRequest(Position origin, Position destination) {}

    public record FareRequest(
        Position origin,
        Position destination,
        boolean surgeApplied,
        boolean promoApplied
    ) {}

    public record MatchRequest(String tripId, String userId, Position origin) {}

    public record MatchDecisionRequest(String tripId, String driverId) {}

    public record RouteResult(double distanceMeters, double durationSeconds, Geometry geometry) {
        public record Geometry(String type, List<List<Double>> coordinates) {}
    }

    public record FareResult(BigDecimal totalFare) {}

    public record MatchResult(String tripId, String userId, String driverId, int attemptedDrivers) {}
}
