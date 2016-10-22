package com.totris.zebra.users.events;


import com.totris.zebra.users.User;

public abstract class UserEvent {

    private User user;

    public UserEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
