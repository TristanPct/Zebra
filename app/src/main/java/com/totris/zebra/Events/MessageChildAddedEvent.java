package com.totris.zebra.Events;


import com.totris.zebra.Models.Group;
import com.totris.zebra.Models.Message;

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
