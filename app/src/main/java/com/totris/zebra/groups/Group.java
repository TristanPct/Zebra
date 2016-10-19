package com.totris.zebra.groups;


import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.totris.zebra.messages.MessageChildAddedEvent;
import com.totris.zebra.messages.EncryptedMessage;
import com.totris.zebra.messages.Message;
import com.totris.zebra.users.User;
import com.totris.zebra.utils.Database;
import com.totris.zebra.utils.EventBus;
import com.totris.zebra.utils.OnlineStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Group implements Serializable {
    private final static String TAG = "Group";

    private static final long serialVersionUID = 8392718937281219L;

    private static DatabaseReference dbRef = Database.getInstance().getReference("groups");

    private String passphrase = "TEMP PASSPHRASE"; // TODO: use a real passphrase

    private String uid;
    private Date createdAt;
    private List<String> usersIds = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
    private boolean messagesLoaded = false;

    public Group() {
        createdAt = new Date();
    }

    public Group(List<User> users) {
        this();
        for (User u : users) {
            usersIds.add(u.getUid());
        }
    }

    public Group(String uid) {
        this();
        this.uid = uid;
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

    @Exclude
    public GroupUser getGroupUser() { //TODO: update all concerned users of the group
        return new GroupUser(usersIds, getUid());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, EncryptedMessage> messages) {
        setMessages(new ArrayList<>(messages.values()));
    }

    @Exclude
    public <T> void setMessages(List<T> messages) {
        messagesLoaded = false;
        this.messages.clear();

        if (messages == null) {
            Log.d(TAG, "setMessages: null");
            return;
        }

        if (messages.size() != 0) {
            if (messages.get(0) instanceof Message) {
                Log.d(TAG, "setMessages: normal messages");
                this.messages = (List<Message>) messages;
            } else if (messages.get(0) instanceof EncryptedMessage) {
                Log.d(TAG, "setMessages: encrypted messages");
                List<EncryptedMessage> encryptedMessages = (List<EncryptedMessage>) messages;
                for (EncryptedMessage em : encryptedMessages) {
                    this.messages.add(decryptMessage(em));
                }
            }
        }

        messagesLoaded = true;
    }

    @Exclude
    public boolean isMessagesLoaded() {
        return messagesLoaded;
    }

    @Exclude
    public Message getLastMessage() {
        if (messages.size() == 0) return null;

        return messages.get(messages.size() - 1);
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public EncryptedMessage encryptedMessage(Message message) {
        return message.encrypt(passphrase);
    }

    public Message decryptMessage(EncryptedMessage encryptedMessage) {
        return encryptedMessage.decrypt(passphrase);
    }

    public static Group getCommonGroup(List<User> users) {
        String commonGroupId = null;

        //TODO: make it work for selection > 1
        for (GroupUser group : User.getCurrent().getGroups()) {
            for (String userId : group.getUsersIds()) {
                if (userId.equals(users.get(0).getUid())) {
                    commonGroupId = group.getGroupId();
                }
            }
        }

        Group group;

        if (commonGroupId != null) {
            group = new Group(commonGroupId);
        } else {
            users.add(User.getCurrent());
            group = new Group(users);
            group.persist();

            for (User u : users) {
                u.registerGroup(group);
                u.persist();
            }
        }

        return group;
    }

    private boolean isDefined() {
        return !(uid == null || uid.trim().equals(""));
    }

    public void persist() {
        Log.d(TAG, "persist: ");

        if (isDefined()) {
            dbRef.child(uid).setValue(this);
            Log.d(TAG, "persisted  exisitng: " + uid);
        } else {
            DatabaseReference groupRef = dbRef.push();
            groupRef.setValue(this);
            uid = groupRef.getKey();
            Log.d(TAG, "persisted: " + uid);
        }
    }

    public DatabaseReference sendMessage(Message message) {
        DatabaseReference tmpRef = dbRef.child(uid).child("messages").push();

        tmpRef.setValue(encryptedMessage(message));

        return tmpRef;
    }

    public DatabaseReference sendImageMessage(Message message, byte[] imageBitmap) {
        DatabaseReference tmpRef = dbRef.child(uid).child("messages").push();

        OnlineStorage.uploadImage(imageBitmap, message.getContent());

        tmpRef.setValue(encryptedMessage(message));

        return tmpRef;
    }

    public void initialize() {
        addChildEventListener();
    }

    public void addChildEventListener() {
        dbRef.child(uid).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EncryptedMessage encryptedMessage = dataSnapshot.getValue(EncryptedMessage.class);
                Message message = decryptMessage(encryptedMessage);

                EventBus.post(new MessageChildAddedEvent(Group.this, message));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                EncryptedMessage encryptedMessage = dataSnapshot.getValue(EncryptedMessage.class);
                Message message = decryptMessage(encryptedMessage);

                Log.d(TAG, "onChildChanged: " + message.getSentAt());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
