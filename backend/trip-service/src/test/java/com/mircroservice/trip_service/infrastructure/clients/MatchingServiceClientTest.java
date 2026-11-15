package com.mircroservice.trip_service.infrastructure.clients;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.mircroservice.trip_service.domain.value_objects.Position;

class MatchingServiceClientTest {
    private static final String BASE_URL = "http://matching-service";
    private static final Position ORIGIN = new Position(10.0, 106.0);
    private static final Position DESTINATION = new Position(10.1, 106.1);

    @Test
    void callsRouteFareMatchAndDecisionEndpointsUsingMatchingServiceContracts() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        MatchingServiceClient client = new MatchingServiceClient(builder, BASE_URL);

        server.expect(requestTo(BASE_URL + "/match/route"))
            .andExpect(method(POST))
            .andExpect(content().json("""
                {"origin":{"latitude":10.0,"longitude":106.0},
                 "destination":{"latitude":10.1,"longitude":106.1}}
                """))
            .andRespond(withSuccess("""
                {"distanceMeters":1000,"durationSeconds":120,
                 "geometry":{"type":"LineString","coordinates":[[106.0,10.0],[106.1,10.1]]}}
                """, APPLICATION_JSON));
        server.expect(requestTo(BASE_URL + "/match/fare"))
            .andExpect(method(POST))
            .andExpect(content().json("""
                {"origin":{"latitude":10.0,"longitude":106.0},
                 "destination":{"latitude":10.1,"longitude":106.1},
                 "surgeApplied":false,"promoApplied":false}
                """))
            .andRespond(withSuccess("{\"totalFare\":5064.14}", APPLICATION_JSON));
        server.expect(requestTo(BASE_URL + "/match"))
            .andExpect(method(POST))
            .andExpect(content().json("""
                {"tripId":"trip-1","userId":"rider-1",
                 "origin":{"latitude":10.0,"longitude":106.0}}
                """))
            .andRespond(withSuccess("""
                {"tripId":"trip-1","userId":"rider-1","driverId":"driver-1","attemptedDrivers":1}
                """, APPLICATION_JSON));
        server.expect(requestTo(BASE_URL + "/match/accept"))
            .andExpect(method(POST))
            .andExpect(content().json("{\"tripId\":\"trip-1\",\"driverId\":\"driver-1\"}"))
            .andRespond(withSuccess());
        server.expect(requestTo(BASE_URL + "/match/confirm"))
            .andExpect(method(POST))
            .andExpect(content().json("{\"tripId\":\"trip-1\",\"driverId\":\"driver-1\"}"))
            .andRespond(withSuccess());
        server.expect(requestTo(BASE_URL + "/match/decline"))
            .andExpect(method(POST))
            .andExpect(content().json("{\"tripId\":\"trip-1\",\"driverId\":\"driver-1\"}"))
            .andRespond(withSuccess());

        var route = client.getRoute(ORIGIN, DESTINATION);
        assertEquals(1_000.0, route.distanceMeters());
        assertEquals(List.of(106.0, 10.0), route.geometry().coordinates().get(0));
        assertEquals("5064.14", client.calculateFare(ORIGIN, DESTINATION).totalFare().toPlainString());
        assertTrue(client.match("trip-1", "rider-1", ORIGIN).isPresent());
        client.accept("trip-1", "driver-1");
        client.confirm("trip-1", "driver-1");
        client.decline("trip-1", "driver-1");

        server.verify();
    }

    @Test
    void mapsNoAvailableDriverResponseToAnEmptyResult() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        MatchingServiceClient client = new MatchingServiceClient(builder, BASE_URL);
        server.expect(requestTo(BASE_URL + "/match"))
            .andExpect(method(POST))
            .andRespond(withStatus(HttpStatus.NO_CONTENT));

        assertTrue(client.match("trip-1", "rider-1", ORIGIN).isEmpty());
        server.verify();
    }
}
