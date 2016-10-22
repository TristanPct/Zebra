package com.totris.zebra.users.events;


import com.totris.zebra.users.User;

public class UserChangedEvent extends UserEvent {

    public UserChangedEvent(User user) {
        super(user);
    }

}
