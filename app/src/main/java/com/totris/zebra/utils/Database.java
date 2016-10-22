package com.totris.zebra.utils;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.groups.Group;
import com.totris.zebra.models.GroupRecord;
import com.totris.zebra.groups.events.GroupAddedEvent;
import com.totris.zebra.groups.events.GroupChangedEvent;
import com.totris.zebra.groups.events.GroupRemovedEvent;
import com.totris.zebra.messages.EncryptedMessage;
import com.totris.zebra.messages.events.MessageAddedEvent;
import com.totris.zebra.messages.events.MessageChangedEvent;
import com.totris.zebra.messages.events.MessageRemovedEvent;
import com.totris.zebra.users.User;
import com.totris.zebra.users.events.UserAddedEvent;
import com.totris.zebra.models.UserRecord;
import com.totris.zebra.users.events.UserChangedEvent;
import com.totris.zebra.users.events.UserRemovedEvent;

import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thomaslecoeur on 11/10/2016.
 */
public class Database {
    private static final String TAG = "Database";

    private static Database instance = new Database();

    private FirebaseDatabase database;

    private ChildEventListener userListener;
    private ChildEventListener groupListener;
    private Map<String, ChildEventListener> groupMessagesListeners = new HashMap<>();
    private ChildEventListener groupUserListener;

    private Database() {
        database = FirebaseDatabase.getInstance();
    }

    public static Database getInstance() {
        return instance;
    }

    public DatabaseReference getReference(String ref) {
        return database.getReference(ref);
    }

    public <T> Promise get(DatabaseReference dbRef, final Class<T> type) {
        final DeferredObject deferred = new DeferredObject();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<T> list = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    T item = postSnapshot.getValue(type);

                    list.add(item);
                }

                deferred.resolve(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                deferred.resolve(list);
            }
        });

        return deferred.promise();
    }

    /**
     * Data synchronization
     */

    public void sync() {

    }

    /**
     * Listeners
     */

    public void initListeners() {
        initListeners(false);
    }

    public void initListeners(boolean start) {
        initUserListener();
        initGroupListener();

        if (start) {
            startListeners();
        }
    }

    public void startListeners() {
        startUserListener();
        startGroupListener();
    }

    public void stopListeners() {
        stopUserListener();
        stopGroupListener();
    }

    /**
     * User listener
     */

    private void initUserListener() {
        userListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                UserRecord.save(user);
                User.add(user);

                EventBus.post(new UserAddedEvent(user));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                UserRecord.save(user);
                User.update(user);

                EventBus.post(new UserChangedEvent(user));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                UserRecord.delete(user);
                User.remove(user);

                EventBus.post(new UserRemovedEvent(user));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void startUserListener() {
        getReference("users").addChildEventListener(userListener);
    }

    private void stopUserListener() {
        getReference("users").removeEventListener(userListener);
    }

    /**
     * Group listener
     */

    private void initGroupListener() {
        groupListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);

                if (!group.getUsersIds().contains(User.getCurrent().getUid())) return;

                group.setUid(dataSnapshot.getKey());

                startGroupMessagesListener(group, dataSnapshot.child("messages"));

                GroupRecord.save(group);
                Group.add(group);
                EventBus.post(new GroupAddedEvent(group));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Group group = dataSnapshot.getValue(Group.class);

                if (!group.getUsersIds().contains(User.getCurrent().getUid())) return;

                group.setUid(dataSnapshot.getKey());

                GroupRecord.save(group);
                Group.update(group);
                EventBus.post(new GroupChangedEvent(group));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);

                if (!group.getUsersIds().contains(User.getCurrent().getUid())) return;

                group.setUid(dataSnapshot.getKey());

                stopGroupMessagesListener(group, dataSnapshot.child("messages"));

                GroupRecord.delete(group);
                Group.remove(group);
                EventBus.post(new GroupRemovedEvent(group));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void startGroupListener() {
        getReference("groups").addChildEventListener(groupListener);
    }

    private void stopGroupListener() {
        getReference("groups").removeEventListener(groupListener);
    }

    /**
     * Message listener
     */

    private ChildEventListener createGroupMessagesListener(final Group group) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EncryptedMessage message = dataSnapshot.getValue(EncryptedMessage.class);

                if (group.getEncryptedMessages().contains(message)) return;

                EventBus.post(new MessageAddedEvent(group, group.decryptMessage(message)));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                EncryptedMessage message = dataSnapshot.getValue(EncryptedMessage.class);

                EventBus.post(new MessageChangedEvent(group, group.decryptMessage(message)));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                EncryptedMessage message = dataSnapshot.getValue(EncryptedMessage.class);

                EventBus.post(new MessageRemovedEvent(group, group.decryptMessage(message)));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void startGroupMessagesListener(Group group, DataSnapshot dataSnapshot) {
        ChildEventListener listener;

        if (!groupMessagesListeners.containsKey(group.getUid())) {
            listener = createGroupMessagesListener(group);
            groupMessagesListeners.put(group.getUid(), listener);
        } else {
            listener = groupMessagesListeners.get(group.getUid());
        }

        dataSnapshot.getRef().addChildEventListener(listener);
    }

    private void stopGroupMessagesListener(Group group, DataSnapshot dataSnapshot) {
        ChildEventListener listener = groupMessagesListeners.get(group.getUid());

        if (listener != null) {
            dataSnapshot.getRef().removeEventListener(listener);
        }
    }
}
