package com.totris.zebra.messages.events;


import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.Message;

public class MessageAddedEvent extends MessageEvent {

    public MessageAddedEvent(Group group, Message message) {
        super(group, message);
    }

}
