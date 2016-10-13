package com.totris.zebra.Models;


import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.Utils.Database;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Group {
    private static String TAG = "Group";

    private List<Message> messages;
    public Date createdAt;
    public String uid;
    private boolean isExisting;
    private static DatabaseReference dbRef = Database.getInstance().getReference("groups");

    public Group() {
        createdAt = new Date();
    }

    public Group(List<User> users) {
        this();
        messages = new ArrayList<Message>();
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

    public static Group getCommonGroup(List<User> users) {
        List<String> commonGroupId = new ArrayList<String>(users.get(0).getGroupsIds());
        commonGroupId.retainAll(users.get(1).getGroupsIds());

        Log.d(TAG, "getCommonGroup: " + commonGroupId.size());

        Group group;

        if (commonGroupId.size() == 1) {
            group = new Group(commonGroupId.get(0));
        } else {
            group = new Group(users);
            group.persist();

            for(User u : users) {
                u.registerGroup(group);
                u.persist();
            }
        }

        //group.initListener();

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

    public void initListener() {
        // Read from the database
        dbRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Group value = dataSnapshot.getValue(Group.class);

                /*if(value.messages != null) {
                    setMessages(value.messages);
                }*/
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
