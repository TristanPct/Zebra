package com.totris.zebra.groups;


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
