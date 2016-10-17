package com.totris.zebra.users;


import com.totris.zebra.groups.GroupUser;

public class UserRegisterGroupEvent {
    private GroupUser groupUser;

    public UserRegisterGroupEvent(GroupUser groupUser) {
        this.groupUser = groupUser;
    }

    public GroupUser getGroupUser() {
        return groupUser;
    }
}
