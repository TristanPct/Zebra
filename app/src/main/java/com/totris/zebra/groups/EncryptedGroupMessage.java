package com.totris.zebra.groups;


import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.EncryptedMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncryptedGroupMessage {
    private final static String TAG = "EncryptedGroupMessage";

    private String uid;
    private Date createdAt;
    private List<String> usersIds = new ArrayList<>();
    private List<EncryptedMessage> messages = new ArrayList<>();

    public EncryptedGroupMessage() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<String> usersIds) {
        this.usersIds = usersIds;
    }

    public List<EncryptedMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<EncryptedMessage> messages) {
        this.messages = messages;
    }

    public Group toGroup() {
        Group group = new Group();

        group.setUid(uid);
        group.setCreatedAt(createdAt);
        group.setUsersIds(getUsersIds());
        group.setMessages(messages);

        return group;
    }
}
