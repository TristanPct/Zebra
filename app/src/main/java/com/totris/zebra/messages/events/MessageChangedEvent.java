package com.totris.zebra.messages.events;


import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.Message;

public class MessageChangedEvent extends MessageEvent {

    public MessageChangedEvent(Group group, Message message) {
        super(group, message);
    }

}
