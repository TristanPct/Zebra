package com.totris.zebra.Models;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.Utils.Authentication;
import com.totris.zebra.Utils.Database;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link User} model.
 * Built over {@link FirebaseUser}.
 * To instantiate a new {@link User} use {@link User#from} with the original {@link FirebaseUser} object.
 * To update user data use update methods (can be chained) and then use {@link User#commit} to push modifications to the database.
 */
public class User {
    private static String TAG = "User";

    private FirebaseUser firebaseUser;
    private static User currentUser = Authentication.getInstance().getCurrentUser();
    private static DatabaseReference dbRef = Database.getInstance().getReference("users");

    private final Deferred deferred = new DeferredObject<>();

    private String username;
    private String mail;
    private String password;
    private String uid;

    public List<String> groupsIds;

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
        if(firebaseUser != null) {
            return firebaseUser.getUid();
        } else {
            return uid;
        }
    }

    public String getUsername() {
        return username;
    }

    public User updateUsername(String username) {
        this.username = username;
        isUsernameUpdated = true;
        return this;
    }

    public String getMail() {
        if(firebaseUser != null) {
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

        if(isUsernameUpdated) {
            dbRef.child(getUid()).setValue(this);
        }
    }

    public void persist() {
        dbRef.child(getUid()).setValue(this);
    }

    public static Promise getList() {
        final DeferredObject deferred = new DeferredObject();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            List<User> list = new ArrayList<User>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
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

    public List<String> getGroupsIds() {
        if(groupsIds != null) {
            return groupsIds;
        } else {
            return new ArrayList<String>();
        }
    }

    public void setGroupsIds(List<String> groupsIds) {
        this.groupsIds = groupsIds;
    }

    public void registerGroup(Group group) {
        if(groupsIds == null) {
            groupsIds = new ArrayList<>();
        }
        groupsIds.add(group.uid);
    }

    public void registerGroup(String uid) {
        if(groupsIds == null) {
            groupsIds = new ArrayList<>();
        }
        groupsIds.add(uid);
    }

    public void initialize() {
        if(dbRef == null) {
            dbRef = Database.getInstance().getReference("users");
        }

        dbRef.child(getUid()).child("groupsIds").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                registerGroup(dataSnapshot.getValue(String.class));
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
