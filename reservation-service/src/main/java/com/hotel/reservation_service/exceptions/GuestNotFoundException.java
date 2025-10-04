package com.hotel.reservation_service.exceptions;

public class GuestNotFoundException extends RuntimeException{
    public GuestNotFoundException(String message){
        super(message);
    }
}
