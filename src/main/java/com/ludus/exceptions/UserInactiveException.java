package com.ludus.exceptions;

public class UserInactiveException extends RuntimeException{
    public UserInactiveException(String message){
        super(message);
    }

}
