package com.totris.zebra.users;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ValueEventListener;
import com.totris.zebra.groups.Group;
import com.totris.zebra.groups.GroupUser;
import com.totris.zebra.models.UserRecord;
import com.totris.zebra.utils.Authentication;
import com.totris.zebra.utils.Database;
import com.totris.zebra.utils.EventBus;
import com.totris.zebra.utils.RsaCrypto;
import com.totris.zebra.utils.RsaEcb;

import org.jdeferred.DoneCallback;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
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
    private PublicKey publicKey;

    private List<GroupUser> groups = new ArrayList<>();
    private int instantiatedGroups = 0;
    private boolean requestGroupUsersInstantiation = false;

    private boolean isUsernameUpdated = false;
    private boolean isMailUpdated = false;
    private boolean isPasswordUpdated = false;
    private String oldUsername;
    private String oldMail;
    private int valuesToCommit = 0;
    private boolean isPublicKeyUpdated;

    private static List<User> users = new ArrayList<>();

    public User() {

    }

    public User(String username) {
        this.username = username;
        this.oldUsername = username;
    }

    public static User from(FirebaseUser firebaseUser) {
        User user = new User();

        user.firebaseUser = firebaseUser;

//        user.initialize();

        user.oldUsername = user.username;
        user.oldMail = user.mail;

        return user;
    }

    public static Promise getById(String uid) {
        final DeferredObject deferred = new DeferredObject();

        if (uid == null || uid.trim().equals("") || getCurrent().getUid().equals(uid)) {
            deferred.resolve(getCurrent());
        } else {
            dbRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    deferred.resolve(dataSnapshot.getValue(User.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    deferred.resolve(null);
                }
            });
        }

        return deferred.promise();
    }

    public static User getCurrent() {
        return currentUser;
    }

    public String getUid() {
        if (firebaseUser != null) {
            return firebaseUser.getUid();
        } else {
            return uid;
        }
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        if (firebaseUser != null) {
            return firebaseUser.getDisplayName();
        } else {
            return username;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User updateUsername(String username) {
        setUsername(username);
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

    public void setMail(String mail) {
        this.mail = mail;
    }

    public User updateMail(String mail) {
        setMail(mail);
        isMailUpdated = true;
        return this;
    }

    public User updatePassword(String password) {
        this.password = password;
        isPasswordUpdated = true;
        return this;
    }

    @Exclude
    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getBase64PublicKey() {
        try {
            return RsaEcb.getPublicKeyString(publicKey);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setBase64PublicKey(String publicKey) {
        try {
            this.publicKey = RsaEcb.getRSAPublicKeyFromString(publicKey);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public User updatePublicKey(Context context) {
        publicKey = RsaCrypto.InitRsaKeys(context);
        Log.d(TAG, "updatePublicKey: " + this.publicKey.toString());
        isPublicKeyUpdated = true;
        return this;
    }

    public List<GroupUser> getGroups() {
        return groups;
    }

    public void requestGroupUserInstantiation(boolean requested) {
        requestGroupUsersInstantiation = requested;
    }

    /**
     * Cache list
     */

    public static List<User> getAll() {
        return getAll(false);
    }

    public static List<User> getAll(boolean refresh) {
        Log.d(TAG, "getAll: refresh: " + (refresh || users.size() == 0));

        if (refresh || users.size() == 0) {
            users.clear();
            List<UserRecord> userRecords = UserRecord.find(UserRecord.class, "");

            User user;
            for (UserRecord record : userRecords) {
                user = new User();
                user.setUid(record.getUid());
                user.setUsername(record.getUsername());
                user.setMail(record.getMail());
                user.setBase64PublicKey(record.getBase64PublicKey());
                users.add(user);
            }
        }

        return users;
    }

    public static void add(User user) {
        boolean added = false;

        for (User u : users) {
            if (u.getUid().equals(user.getUid())) {
                users.remove(u);
                users.add(user);
                added = true;
                break;
            }
        }

        if (!added) {
            users.add(user);
        }
    }

    public static void update(User user) {
        for (User u : users) {
            if (u.getUid().equals(user.getUid())) {
                users.remove(u);
                users.add(user);
                break;
            }
        }
    }

    public static void remove(User user) {
        users.remove(user);
    }

    @Exclude
    public Promise getInstantiatedGroupUsers() {
        Log.d(TAG, "getInstantiatedGroupUsers: " + instantiatedGroups + " / " + groups.size());

        final DeferredObject deferred = new DeferredObject();

        if (instantiatedGroups == groups.size()) {
            deferred.resolve(groups);
            return deferred.promise();
        }

        instantiatedGroups = 0;

        requestGroupUserInstantiation(true);

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

    @Exclude
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

        EventBus.post(new UserRegisterGroupEvent(group));
    }

    public void registerGroup(Group group) {
        registerGroup(group.getGroupUser());
    }

    public void commit() {
        commit(new OnCommitListener() {
            @Override
            public void onComplete(boolean success, List<String> errors) {

            }
        });
    }

    public void commit(final OnCommitListener listener) {
        valuesToCommit = 0;
        valuesToCommit += isUsernameUpdated ? 1 : 0;
        valuesToCommit += isMailUpdated ? 1 : 0;
        valuesToCommit += isPasswordUpdated ? 1 : 0;

        final List<String> errors = new ArrayList<>();

        if (isUsernameUpdated) {
            UserProfileChangeRequest updates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            firebaseUser.updateProfile(updates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isUsernameUpdated = !task.isSuccessful();
                            valuesToCommit--;

                            if (isUsernameUpdated) {
                                errors.add("ERROR_USERNAME");
                                username = oldUsername;
                            }

                            if (valuesToCommit == 0) {
                                persist();
                                listener.onComplete(errors.size() == 0, errors);
                            }
                        }
                    });
        }

        if (isMailUpdated) {
            firebaseUser.updateEmail(mail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isMailUpdated = !task.isSuccessful();
                            valuesToCommit--;

                            if (isMailUpdated) {
                                errors.add("ERROR_EMAIL");
                                mail = oldMail;
                            }

                            if (valuesToCommit == 0) {
                                persist();
                                listener.onComplete(errors.size() == 0, errors);
                            }
                        }
                    });
        }

        if (isPasswordUpdated) {
            firebaseUser.updatePassword(password)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            isPasswordUpdated = !task.isSuccessful();
                            valuesToCommit--;

                            if (isPasswordUpdated) {
                                errors.add("ERROR_PASSWORD");
                            }

                            if (valuesToCommit == 0) {
                                listener.onComplete(errors.size() == 0, errors);
                            }
                        }
                    });
        }

        if(isPublicKeyUpdated) {
            valuesToCommit--;

            if (valuesToCommit == 0) {
                persist();
            }
        }

        // update database linked model

//        if (isUsernameUpdated || isMailUpdated) {
//            persist();
//        }
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

//    public void initialize() {
//        if (dbRef == null) {
//            dbRef = Database.getInstance().getReference("users");
//        }
//
//        Log.d(TAG, "initialize: " + getUid());
//
//        dbRef.child(getUid()).child("groups").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.d(TAG, "onChildAdded: group added : " + dataSnapshot.getValue(GroupUser.class));
//                registerGroup(dataSnapshot.getValue(GroupUser.class));
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

    public interface OnCommitListener {
        void onComplete(boolean success, List<String> errors);
    }
}
