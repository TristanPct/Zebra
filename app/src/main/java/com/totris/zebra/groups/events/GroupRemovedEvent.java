package com.totris.zebra.groups.events;


import com.totris.zebra.groups.Group;

public class GroupRemovedEvent extends GroupEvent {

    public GroupRemovedEvent(Group group) {
        super(group);
    }

}
