package com.totris.zebra.Models;


import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.totris.zebra.Events.MessageChildAddedEvent;
import com.totris.zebra.Utils.Database;
import com.totris.zebra.Utils.EventBus;

import org.jdeferred.Promise;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group  implements Serializable {
    private static String TAG = "Group";

    private static final long serialVersionUID = 8392718937281219L;

    private List<Message> messages = new ArrayList<>();
    public Date createdAt;
    public String uid;
    public List<String> usersIds = new ArrayList<>();
    private static DatabaseReference dbRef = Database.getInstance().getReference("groups");

    public Group() {
        createdAt = new Date();
    }

    public Group(List<User> users) {
        this();

        for(User u: users) {
            usersIds.add(u.getUid());
        }
    }

    public Group(String uidVar) {
        this();
        uid = uidVar;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public static Group getCommonGroup(List<User> users) {
        List<GroupUser> groups = new ArrayList<>(User.getCurrent().getGroups());

        String commonGroupId = null;

        for(GroupUser group: User.getCurrent().getGroups()) {
            for (String userId: group.getUsersIds()) {
                if(userId == users.get(0).getUid()) {
                    commonGroupId = group.getGroupId();
                    Log.d(TAG, "getCommonGroup: le group " + group.getGroupId() + " est un groupe commun");
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

            for(User u : users) {
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

        if(isDefined()) {
            dbRef.child(uid).setValue(this);
            Log.d(TAG, "persisted  exisitng: " + uid);
        } else {
            DatabaseReference groupRef = dbRef.push();
            groupRef.setValue(this);
            uid = groupRef.getKey();
            Log.d(TAG, "persisted: " + uid);
        }
    }

    public void sendMessage(Message message) {
        dbRef.child(uid).child("messages").push().setValue(message.encrypt("TEMP PASSPHRASE"));
    }

    public GroupUser getGroupUser() { //TODO: update all concerned users of the group
        return new GroupUser(usersIds, getUid());
    }

    public void initialize() {
        addChildEventListener();
    }

    public void addChildEventListener() {
        dbRef.child(uid).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EncryptedMessage encryptedMessage = dataSnapshot.getValue(EncryptedMessage.class);
                Message message = encryptedMessage.decrypt("TEMP PASSPHRASE"); // TODO: use a real passphrase

                EventBus.post(new MessageChildAddedEvent(Group.this, message));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
