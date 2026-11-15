package com.gateway.api_gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {
	@Autowired
	private RouteLocator routeLocator;

	@Test
	void contextLoads() {
	}

	@Test
	void configuresRoutesForAllServiceApis() {
		var routeIds = routeLocator.getRoutes()
			.map(route -> route.getId())
			.collectList()
			.block();

		assertThat(routeIds).contains(
			"matching-service",
			"matching-service-api",
			"matching-auth",
			"matching-users",
			"matching-drivers",
			"matching-active-drivers",
			"trip-service",
			"payment-service"
		);
	}

}
