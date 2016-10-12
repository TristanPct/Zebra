package com.totris.zebra.Events;


import com.totris.zebra.Models.Message;

public class MessageChildAddedEvent {
    private Message message;

    public MessageChildAddedEvent(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
