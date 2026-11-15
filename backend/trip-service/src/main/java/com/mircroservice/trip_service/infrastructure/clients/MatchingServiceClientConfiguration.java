package com.mircroservice.trip_service.infrastructure.clients;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class MatchingServiceClientConfiguration {

    @Bean
    @LoadBalanced
    public RestClient.Builder matchingServiceRestClientBuilder() {
        return RestClient.builder();
    }
}
