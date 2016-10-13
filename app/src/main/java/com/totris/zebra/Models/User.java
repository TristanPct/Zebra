package com.totris.zebra.Models;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.Utils.Authentication;
import com.totris.zebra.Utils.Database;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link User} model.
 * Built over {@link FirebaseUser}.
 * To instantiate a new {@link User} use {@link User#from} with the original {@link FirebaseUser} object.
 * To update user data use update methods (can be chained) and then use {@link User#commit} to push modifications to the database.
 */
public class User {
    private final static String TAG = "User";

    private static User currentUser = Authentication.getInstance().getCurrentUser();
    private static DatabaseReference dbRef = Database.getInstance().getReference("users");

    private FirebaseUser firebaseUser;
    private String uid;
    private String username;
    private String mail;
    private String password;

    private List<GroupUser> groups = new ArrayList<>();
    private int instantiatedGroups = 0;
    private boolean requestGroupUsersInstantiation = false;

    private boolean isUsernameUpdated = false;
    private boolean isMailUpdated = false;
    private boolean isPasswordUpdated = false;

    public User() {

    }

    public User(String username) {
        this.username = username;
    }

    public static User from(FirebaseUser firebaseUser) {
        User user = new User();

        user.firebaseUser = firebaseUser;

        user.initialize();

        return user;
    }

    public static User getCurrent() {
        return currentUser;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        if (firebaseUser != null) {
            return firebaseUser.getUid();
        } else {
            return uid;
        }
    }

    public String getUsername() {
        if (firebaseUser != null) {
            return firebaseUser.getDisplayName();
        } else {
            return username;
        }
    }

    public User updateUsername(String username) {
        this.username = username;
        isUsernameUpdated = true;
        return this;
    }

    public String getMail() {
        if (firebaseUser != null) {
            return firebaseUser.getEmail();
        } else {
            return mail;
        }
    }

    public User updateMail(String mail) {
        this.mail = mail;
        isMailUpdated = true;
        return this;
    }

    public User updatePassword(String password) {
        this.password = password;
        isPasswordUpdated = true;
        return this;
    }

    public List<GroupUser> getGroups() {
        return groups;
    }

    public Promise getInstantiatedGroupUsers() {
        Log.d(TAG, "getInstantiatedGroupUsers: " + groups.size());
        instantiatedGroups = 0;

        final DeferredObject deferred = new DeferredObject();

        for (final GroupUser groupUser : groups) {
            Log.d(TAG, "getInstantiatedGroupUsers: group: " + groupUser.getGroupId());
            getInstantiatedGroupUser(groupUser).done(new DoneCallback() {
                @Override
                public void onDone(Object result) {
                    instantiatedGroups++;
                    if (instantiatedGroups == groups.size()) {
                        deferred.resolve(groups);
                    }
                }
            });
        }

        return deferred.promise();
    }

    private Promise getInstantiatedGroupUser(final GroupUser groupUser) {
        final DeferredObject deferred = new DeferredObject();

        Log.d(TAG, "getInstantiatedGroupUsers: group: " + groupUser.getGroupId());
        groupUser.getInstantiatedGroupUsers().done(new DoneCallback() {
            @Override
            public void onDone(Object result) {
                Log.d(TAG, "getInstantiatedGroupUsers: done" + groupUser.getGroupId());
                deferred.resolve(groups);
            }
        });

        return deferred.promise();
    }

    public void registerGroup(GroupUser group) {
        groups.add(group);
        getInstantiatedGroupUser(group);
//        if (requestGroupUsersInstantiation) {
//            getInstantiatedGroupUser(group);
//        }
    }

    public void registerGroup(Group group) {
        registerGroup(group.getGroupUser());
    }

    public void commit() {
        if (isUsernameUpdated) {
            UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            firebaseUser.updateProfile(updates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isUsernameUpdated = !task.isSuccessful();
                        }
                    });
        }

        if (isMailUpdated) {
            firebaseUser.updateEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isMailUpdated = !task.isSuccessful();
                        }
                    });
        }

        if (isPasswordUpdated) {
            firebaseUser.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isPasswordUpdated = !task.isSuccessful();
                        }
                    });
        }

        // update database linked model

        if (isUsernameUpdated || isMailUpdated) {
            persist();
        }
    }

    public void persist() {
        dbRef.child(getUid()).setValue(this);
    }

    public static Promise getList() {
        final DeferredObject deferred = new DeferredObject();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<User> list = new ArrayList<>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);

                    list.add(user);
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

    public Promise fetchGroupsIds() {
        return Database.getInstance().get(dbRef.child(getUid()).child("groupsIds"), String.class);
    }

    public Promise fetchGroups() {
        final DeferredObject deferred = new DeferredObject();

        fetchGroupsIds().done(new DoneCallback() {
            @Override
            public void onDone(Object result) {

                deferred.resolve(result);
            }
        });

        return deferred.promise();
    }

    public void initialize() {
        if (dbRef == null) {
            dbRef = Database.getInstance().getReference("users");
        }

        Log.d(TAG, "initialize: " + getUid());

        dbRef.child(getUid()).child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded: group added : " + dataSnapshot.getValue(GroupUser.class));
                registerGroup(dataSnapshot.getValue(GroupUser.class));
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
