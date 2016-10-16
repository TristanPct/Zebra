package com.totris.zebra.messages;


import com.totris.zebra.messages.Message;

import java.util.List;

public class MessageDataChangeEvent {
    private List<Message> messages;

    public MessageDataChangeEvent(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
