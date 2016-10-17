package com.totris.zebra.messages;


import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.Message;

public class MessageChildAddedEvent {
    private Group group;
    private Message message;

    public MessageChildAddedEvent(Group group, Message message) {
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
