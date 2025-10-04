package com.hotel.room_service.exception;

public class RoomNotFoundException extends RuntimeException{
    public RoomNotFoundException(String msg){
        super(msg);
    }
}
