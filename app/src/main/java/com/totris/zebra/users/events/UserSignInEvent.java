package com.totris.zebra.users.events;


import com.totris.zebra.users.User;

public class UserSignInEvent extends UserEvent {

    public UserSignInEvent(User user) {
        super(user);
    }

}
