package com.totris.zebra.groups.events;


import com.totris.zebra.groups.Group;

public class GroupAddedEvent extends GroupEvent {

    public GroupAddedEvent(Group group) {
        super(group);
    }

}
