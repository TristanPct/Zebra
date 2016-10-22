package com.totris.zebra.groups.events;


import com.totris.zebra.groups.Group;

public abstract class GroupEvent {

    private Group group;

    public GroupEvent(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

}
