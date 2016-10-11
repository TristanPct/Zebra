package com.totris.zebra.Models;


import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.totris.zebra.Utils.Authentication;

/**
 * {@link User} model.
 * Built over {@link FirebaseUser}.
 * To instantiate a new {@link User} use {@link User#from} with the original {@link FirebaseUser} object.
 * To update user data use update methods (can be chained) and then use {@link User#commit} to push modifications to the database.
 */
public class User {
    private FirebaseUser firebaseUser;

    private String username;
    private String mail;
    private String password;

    private boolean isUsernameUpdated = false;
    private boolean isMailUpdated = false;
    private boolean isPasswordUpdated = false;

    private User() {

    }

    public static User from(FirebaseUser firebaseUser) {
        User user = new User();

        user.firebaseUser = firebaseUser;

        return user;
    }

    public String getUid() {
        return firebaseUser.getUid();
    }

    public String getUsername() {
        return firebaseUser.getDisplayName();
    }

    public User updateUsername(String username) {
        this.username = username;
        isUsernameUpdated = true;
        return this;
    }

    public String getMail() {
        return firebaseUser.getEmail();
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
    }
}
