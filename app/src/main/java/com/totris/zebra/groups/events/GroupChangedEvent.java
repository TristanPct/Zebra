package com.totris.zebra.groups.events;


import com.totris.zebra.groups.Group;

public class GroupChangedEvent extends GroupEvent {

    public GroupChangedEvent(Group group) {
        super(group);
    }

}
