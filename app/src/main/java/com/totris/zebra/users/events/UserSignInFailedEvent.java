package com.totris.zebra.users.events;


public class UserSignInFailedEvent {

    private String message;

    public UserSignInFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
