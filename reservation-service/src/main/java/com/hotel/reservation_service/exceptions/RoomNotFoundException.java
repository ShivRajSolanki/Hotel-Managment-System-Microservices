package com.hotel.reservation_service.exceptions;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String message){
        super(message);
    }
}
