package com.hotel.reservation_service.exceptions;

public class ExceedsCapacityException extends RuntimeException {
    public ExceedsCapacityException(String message) {
        super(message);
    }
}
