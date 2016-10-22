package com.totris.zebra.users.events;


import com.totris.zebra.users.User;

public class UserRemovedEvent extends UserEvent {

    public UserRemovedEvent(User user) {
        super(user);
    }

}
