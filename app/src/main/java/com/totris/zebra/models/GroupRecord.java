package com.totris.zebra.models;


import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.dsl.Unique;
import com.totris.zebra.groups.Group;
import com.totris.zebra.messages.EncryptedMessage;
import com.totris.zebra.messages.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroupRecord extends SugarRecord {

    private static ObjectMapper mapper = new ObjectMapper();

    @Unique
    private String uid;
    private Date createdAt;
    private String usersIdsJson;
    private String messagesJson;

    @Ignore
    private List<UserRecord> users = new ArrayList<>();
    @Ignore
    private List<EncryptedMessage> messages = new ArrayList<>();

    public GroupRecord() {

    }

    public GroupRecord(String uid, Date createdAt) {
        this.uid = uid;
        this.createdAt = createdAt;
    }

    public GroupRecord(String uid, Date createdAt, String messagesJson) {
        this.uid = uid;
        this.createdAt = createdAt;
        this.messagesJson = messagesJson;
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

    public void setUsersIdsJson(String usersIdsJson) {
        this.usersIdsJson = usersIdsJson;

        List<String> ids;
        try {
            ids = mapper.readValue(usersIdsJson, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            ids = new ArrayList<>();
        }

        UserRecord user;
        for (String id : ids) {
            user = UserRecord.findByUid(id);
            if (user != null) {
                users.add(user);
            }
        }
    }

    public List<UserRecord> getUsers() {
        return users;
    }

    public void setUsersIds(List<String> usersIds) {
        users.clear();

        for (String userId : usersIds) {
            users.add(UserRecord.findByUid(userId));
        }

        try {
            usersIdsJson = mapper.writeValueAsString(usersIds);
        } catch (JsonProcessingException e) {
            usersIdsJson = "";
        }
    }

    public void setUsers(List<UserRecord> users) {
        this.users = users;

        List<String> usersIds = new ArrayList<>();

        for (UserRecord user : users) {
            usersIds.add(user.getUid());
        }

        try {
            usersIdsJson = mapper.writeValueAsString(usersIds);
        } catch (JsonProcessingException e) {
            usersIdsJson = "";
        }
    }

    public void setMessagesJson(String messagesJson) {
        this.messagesJson = messagesJson;

        try {
            messages = mapper.readValue(messagesJson, new TypeReference<List<EncryptedMessage>>() {
            });
        } catch (IOException e) {
            messages = new ArrayList<>();
        }
    }

    public List<EncryptedMessage> getMessages() {
        return messages;
    }

    public <T> void setMessages(List<T> messages) {
        this.messages.clear();

        if (messages == null) {
            messagesJson = "";
            return;
        }

        if (messages.size() != 0) {
            if (messages.get(0) instanceof EncryptedMessage) {
                this.messages = (List<EncryptedMessage>) messages;
            } else if (messages.get(0) instanceof Message) {
                List<Message> decryptedMessages = (List<Message>) messages;
                for (Message dm : decryptedMessages) {
                    this.messages.add(dm.encrypt("TEMP PASSPHRASE")); // TODO: use a real passphrase
                }
            }
        }

        try {
            messagesJson = mapper.writeValueAsString(this.messages);
        } catch (JsonProcessingException e) {
            messagesJson = "";
        }
    }

    public static GroupRecord findByUid(String uid) {
        List<GroupRecord> results = GroupRecord.find(GroupRecord.class, "uid = ?", uid);
        return results.size() != 0 ? results.get(0) : null;
    }

    @Override
    public long save() {
        Log.d("GroupRecord", "save: uid: " + uid);
        GroupRecord group = findByUid(uid);

        if (group != null) {
            setId(group.getId());
        }

        return super.save();
    }

    @Override
    public boolean delete() {
        GroupRecord group = findByUid(uid);

        if (group != null) {
            setId(group.getId());
        }

        return super.delete();
    }

    public static long save(Group group) {
        GroupRecord record = new GroupRecord(group.getUid(), group.getCreatedAt());
        record.setUsersIds(group.getUsersIds());
        record.setMessages(group.getMessages());

        return record.save();
    }

    public static boolean delete(Group group) {
        GroupRecord record = new GroupRecord(group.getUid(), group.getCreatedAt());

        return record.delete();
    }

}
