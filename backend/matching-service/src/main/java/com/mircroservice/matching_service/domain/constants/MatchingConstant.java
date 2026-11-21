package com.mircroservice.matching_service.domain.constants;

import java.time.Duration;

public class MatchingConstant {
    static public final int MAX_MATCHING_ATTEMPT = 3;
    static public final Double SEARCH_RADIUS = 5_000.0;
    static public final Duration OFFER_LIVE_DURATION = Duration.ofSeconds(30);
    static public final Duration MATCHING_LIVE_DURATION = Duration.ofMinutes(2);
}
