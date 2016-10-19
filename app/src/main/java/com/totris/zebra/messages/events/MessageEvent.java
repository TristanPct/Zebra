package com.totris.zebra.messages.events;


import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.Message;

public abstract class MessageEvent {

    private Group group;
    private Message message;

    public MessageEvent(Group group, Message message) {
        this.group = group;
        this.message = message;
    }

    public Group getGroup() {
        return group;
    }

    public Message getMessage() {
        return message;
    }
}
