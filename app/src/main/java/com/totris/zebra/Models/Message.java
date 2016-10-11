package com.totris.zebra.Models;

import java.util.Date;

public class Message {
    private String content;
    private MessageType type;
    private int senderId;
    private int groupId;
    private Date createdAt;
    private Date sentAt;
    private Date receiveAt;

    public Message() {

    }

    public Message(String content, MessageType type, int senderId, int groupId, Date createdAt, Date sentAt, Date receiveAt) {
        this();
        setContent(content);
        setType(type);
        setSenderId(senderId);
        setGroupId(groupId);
        setCreatedAt(createdAt);
        setSentAt(sentAt);
        setReceiveAt(receiveAt);
    }

    public Message(String content, MessageType type, int senderId, int groupId) {
        this(content, type, senderId, groupId, new Date(), null, null);
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

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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
}
