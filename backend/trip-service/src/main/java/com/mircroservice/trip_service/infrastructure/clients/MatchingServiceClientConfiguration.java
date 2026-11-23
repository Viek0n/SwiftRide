package com.mircroservice.trip_service.infrastructure.clients;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class MatchingServiceClientConfiguration {

    // For calling other services through Eureka
    @Bean
    @LoadBalanced
    RestClient.Builder matchingServiceRestClientBuilder() {
        return RestClient.builder();
    }

    // For normal HTTP requests, without service discovery
    @Bean
    @Primary 
    RestClient.Builder normalRestClientBuilder() {
        return RestClient.builder();
    }
}
