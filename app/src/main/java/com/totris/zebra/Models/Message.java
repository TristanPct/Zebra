package com.totris.zebra.Models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.Events.MessageDataChangeEvent;
import com.totris.zebra.Utils.AesCrypto;
import com.totris.zebra.Utils.Database;
import com.totris.zebra.Utils.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message implements Serializable {
    private static final long serialVersionUID = 199509525759554827L;

    private static DatabaseReference dbRef = Database.getInstance().getReference("messages");

    private String content;
    private MessageType type;
    private String senderId;
    private Date createdAt;
    private Date sentAt;
    private Date receiveAt;

    public Message() {

    }

    public Message(String content, MessageType type, String senderId, Date createdAt, Date sentAt, Date receiveAt) {
        this();
        setContent(content);
        setType(type);
        setSenderId(senderId);
        setCreatedAt(createdAt);
        setSentAt(sentAt);
        setReceiveAt(receiveAt);
    }

    public Message(String content, MessageType type) {
        this(content, type, User.getCurrent().getUid(), new Date(), null, null);
    }

    public static void initialize() {
        addValueEventListener();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Date getReceiveAt() {
        return receiveAt;
    }

    public void setReceiveAt(Date receiveAt) {
        this.receiveAt = receiveAt;
    }

    public EncryptedMessage encrypt(String passphrase) {
        return new EncryptedMessage(AesCrypto.encrypt(this, passphrase));
    }

    public static Message decrypt(String message, String passphrase, String salt) {
        return AesCrypto.decrypt(message, passphrase, salt, Message.class);
    }

    private static void addValueEventListener() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Message> messages = new ArrayList<>();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    EncryptedMessage encryptedMessage = postSnapshot.getValue(EncryptedMessage.class);
                    messages.add(encryptedMessage.decrypt("TEMP PASSPHRASE")); // TODO: use a real passphrase
                }

                EventBus.post(new MessageDataChangeEvent(messages));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
