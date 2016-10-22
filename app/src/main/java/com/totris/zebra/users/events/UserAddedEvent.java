package com.totris.zebra.users.events;


import com.totris.zebra.users.User;

public class UserAddedEvent extends UserEvent {

    public UserAddedEvent(User user) {
        super(user);
    }

}
