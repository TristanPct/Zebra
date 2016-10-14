package com.totris.zebra.Events;


import com.totris.zebra.Models.GroupUser;

public class UserRegisterGroupEvent {
    private GroupUser groupUser;

    public UserRegisterGroupEvent(GroupUser groupUser) {
        this.groupUser = groupUser;
    }

    public GroupUser getGroupUser() {
        return groupUser;
    }
}
