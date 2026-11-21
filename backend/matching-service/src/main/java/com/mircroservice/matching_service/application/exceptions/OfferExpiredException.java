package com.mircroservice.matching_service.application.exceptions;

public class OfferExpiredException 
        extends OfferException{
    public OfferExpiredException(String offerId) {
        super("Offer : " + offerId + " is expired");
    }
}
