package com.mircroservice.matching_service.application.port;

import com.mircroservice.matching_service.domain.dto.requests.TripOfferRequest;

public interface TripOfferNotifier {
    void notifyDriver(TripOfferRequest event);
}