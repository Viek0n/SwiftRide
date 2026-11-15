package com.mircroservice.matching_service.infrastructure.routing;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Component
public class OsrmRouteInfoProvider implements RouteInfoProvider {
    private final RestClient restClient;
    private final String baseUrl;

    public OsrmRouteInfoProvider(
        @Value("${matching.osrm.base-url:https://router.project-osrm.org}") String baseUrl
    ) {
        this.baseUrl = baseUrl.replaceAll("/+$", "");
        this.restClient = RestClient.builder().build();
    }

    @Override
    public RouteInfo getRoute(Position origin, Position destination) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Origin and destination are required");
        }

        String coordinates = origin.longitude() + "," + origin.latitude()
            + ";" + destination.longitude() + "," + destination.latitude();
        URI uri = URI.create(
            baseUrl + "/route/v1/driving/" + coordinates + "?overview=full&geometries=geojson"
        );

        OsrmResponse response = restClient.get()
            .uri(uri)
            .retrieve()
            .body(OsrmResponse.class);

        if (response == null || !"Ok".equals(response.code())
            || response.routes() == null || response.routes().isEmpty()) {
            throw new IllegalStateException("OSRM did not return a route");
        }

        OsrmRoute route = response.routes().get(0);
        if (route.distance() == null || route.duration() == null || route.geometry() == null
            || route.geometry().coordinates() == null || route.geometry().coordinates().isEmpty()) {
            throw new IllegalStateException("OSRM route is missing distance, duration, or geometry");
        }
        return new RouteInfo(
            route.distance(),
            route.duration(),
            route.geometry().coordinates()
        );
    }

    private record OsrmResponse(String code, List<OsrmRoute> routes) {}

    private record OsrmRoute(Double distance, Double duration, OsrmGeometry geometry) {}

    private record OsrmGeometry(String type, List<List<Double>> coordinates) {}
}
