package com.totris.zebra.messages.events;


import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.Message;

public class MessageRemovedEvent extends MessageEvent {

    public MessageRemovedEvent(Group group, Message message) {
        super(group, message);
    }

}
