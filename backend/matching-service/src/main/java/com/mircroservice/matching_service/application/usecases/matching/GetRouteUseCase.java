package com.mircroservice.matching_service.application.usecases.matching;

import org.springframework.stereotype.Service;

import com.mircroservice.matching_service.application.abstractions.RouteInfoProvider;
import com.mircroservice.matching_service.domain.value_objects.Position;

@Service
public class GetRouteUseCase {
    private final RouteInfoProvider routeInfoProvider;

    public GetRouteUseCase(RouteInfoProvider routeInfoProvider) {
        this.routeInfoProvider = routeInfoProvider;
    }

    public RouteInfoProvider.RouteInfo execute(Position origin, Position destination) {
        RouteInfoProvider.RouteInfo route = routeInfoProvider.getRoute(origin, destination);
        if (route.geometryCoordinates().isEmpty()) {
            throw new IllegalStateException("Route provider did not return route geometry");
        }
        return route;
    }
}
