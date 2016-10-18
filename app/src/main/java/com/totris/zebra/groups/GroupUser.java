package com.totris.zebra.groups;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.utils.Database;
import com.totris.zebra.utils.EventBus;
import com.totris.zebra.users.User;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thomaslecoeur on 13/10/2016.
 */

public class GroupUser {
    private static final String TAG = "GroupUser";

    private static DatabaseReference usersDbRef = Database.getInstance().getReference("users");
    private static DatabaseReference groupsDbRef = Database.getInstance().getReference("groups");

    private List<String> usersIds = new ArrayList<>();
    private String groupId;

    private List<User> users = new ArrayList<>();
    private Group group;
    private boolean usersLoaded = false;
    private boolean groupLoaded = false;

    public GroupUser() {

    }

    public GroupUser(List<User> users, Group group) {
        this();

        this.users = users;
        this.group = group;
        usersLoaded = true;
        groupLoaded = true;

        for (User u : users) {
            this.usersIds.add(u.getUid());
        }
        this.groupId = group.getUid();

        EventBus.post(new GroupUserInstantiateEvent(this));
    }

    public GroupUser(List<String> usersIds, String groupId) {
        this();
        this.usersIds = usersIds;
        this.groupId = groupId;
    }

    public List<String> getUsersIds() {
        return usersIds;
    }

    public String getGroupId() {
        return groupId;
    }

    @Exclude
    public String getUid() {
        String uid = getGroupId() + ":";

        for (String userId : getUsersIds()) {
            uid += userId + "|";
        }

        return uid;
    }

    @Exclude
    public List<User> getUsers() {
        return users;
    }

    @Exclude
    public Group getGroup() {
        return group;
    }

    @Exclude
    public String getTitle() {
        String title = "";

        for (User u : getUsers()) {
            if (!User.getCurrent().getUid().equals(u.getUid())) {
                title += u.getUsername() + ", ";
            }
        }
        if (title.length() != 0) {
            title = title.substring(0, title.length() - 2);
        }

        return title;
    }

    @Exclude
    public Promise getInstantiatedGroupUsers() {
        groupLoaded = false;
        usersLoaded = false;

        final DeferredObject deferred = new DeferredObject();

        getInstantiatedGroup().done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                groupLoaded = true;
                Log.d(TAG, "onDone: group");
                if (groupLoaded && usersLoaded) {
                    deferred.resolve(GroupUser.this);
                    EventBus.post(new GroupUserInstantiateEvent(GroupUser.this));
                }
            }
        });

        getInstantiatedUsers().done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                usersLoaded = true;
                Log.d(TAG, "onDone: users");
                if (groupLoaded && usersLoaded) {
                    deferred.resolve(GroupUser.this);
                    EventBus.post(new GroupUserInstantiateEvent(GroupUser.this));
                }
            }
        });

        return deferred.promise();
    }

    @Exclude
    private Promise getInstantiatedGroup() {
        final DeferredObject deferred = new DeferredObject();

        if (group != null) {
            deferred.resolve(group);
            return deferred.promise();
        }

        groupsDbRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getInstantiatedGroup#onDataChange: " + dataSnapshot);
//                group = dataSnapshot.getValue(EncryptedGroupMessage.class).toGroup(); // DatabaseException: Expected a List while deserializing, but got a class java.util.HashMap

                group = new Group();

                group.setUid(dataSnapshot.getKey());
                group.setCreatedAt(dataSnapshot.child("createdAt").getValue(Date.class));
                GenericTypeIndicator<List<String>> usersIdsT = new GenericTypeIndicator<List<String>>() {};
                group.setUsersIds(dataSnapshot.child("usersIds").getValue(usersIdsT));
//                group.setMessages(dataSnapshot.child("messages").getValue(encryptedMessagesT)); // DatabaseException: Expected a List while deserializing, but got a class java.util.HashMap
                // FIX: DatabaseException: Expected a List while deserializing, but got a class java.util.HashMap
//                List<EncryptedMessage> encryptedMessages = new ArrayList<>();
//                for (DataSnapshot messageSnapshot : dataSnapshot.child("messages").getChildren()) {
//                    encryptedMessages.add(messageSnapshot.getValue(EncryptedMessage.class));
//                }
//                Log.d(TAG, "getInstantiatedGroup#onDataChange: encryptedMessages: " + encryptedMessages.size());
//                group.setMessages(encryptedMessages); //TODO: do not load messages here ?

                deferred.resolve(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.resolve(users);
            }
        });

        return deferred.promise();
    }

    @Exclude
    private Promise getInstantiatedUsers() {
        final DeferredObject deferred = new DeferredObject();

        if (users.size() >= usersIds.size()) {
            deferred.resolve(users);
            return deferred.promise();
        }

        for (String userId : usersIds) {
            Log.d(TAG, "getInstantiatedUsers: " + userId);
            getInstantiatedUser(userId).done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    Log.d(TAG, "getInstantiatedUsers#onDone: " + users.size() + " / " +  usersIds.size());
                    if (users.size() >= usersIds.size()) {
                        deferred.resolve(users);
                    }
                }
            });
        }

        return deferred.promise();
    }

    @Exclude
    private Promise getInstantiatedUser(String id) {
        final DeferredObject deferred = new DeferredObject();

        usersDbRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "getInstantiatedUser#onDataChange: " + dataSnapshot);
                    users.add(dataSnapshot.getValue(User.class));

                deferred.resolve(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.resolve(users);
            }
        });

        return deferred.promise();
    }
}
