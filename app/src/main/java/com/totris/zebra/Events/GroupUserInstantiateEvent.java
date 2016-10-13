package com.totris.zebra.Events;


import com.totris.zebra.Models.GroupUser;

public class GroupUserInstantiateEvent {
    private GroupUser groupUser;

    public GroupUserInstantiateEvent() {

    }

    public GroupUserInstantiateEvent(GroupUser groupUser) {
        this.groupUser = groupUser;
    }

    public GroupUser getGroupUser() {
        return groupUser;
    }
}
