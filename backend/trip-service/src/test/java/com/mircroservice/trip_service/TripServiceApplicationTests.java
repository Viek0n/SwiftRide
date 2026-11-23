package com.mircroservice.trip_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
	"MONGODB_URI=mongodb://localhost:27017/swiftride_trip_test",
	"MONGODB_DATABASE=swiftride_trip_test"
})
class TripServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
