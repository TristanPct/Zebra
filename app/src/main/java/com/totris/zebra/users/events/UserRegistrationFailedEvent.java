package com.totris.zebra.users.events;


public class UserRegistrationFailedEvent {

    private String message;

    public UserRegistrationFailedEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
