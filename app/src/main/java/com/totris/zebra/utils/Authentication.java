package com.totris.zebra.utils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Subscribe;
import com.totris.zebra.base.events.ApplicationEnterBackgroundEvent;
import com.totris.zebra.base.events.ApplicationEnterForegroundEvent;
import com.totris.zebra.users.User;
import com.totris.zebra.users.events.UserRegistrationFailedEvent;
import com.totris.zebra.users.events.UserSignInEvent;
import com.totris.zebra.users.events.UserSignInFailedEvent;
import com.totris.zebra.users.events.UserSignOutEvent;

public class Authentication {
    private static final String TAG = "Authentication";

    private static Authentication instance = new Authentication();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;

    private boolean initialized = false;
    private boolean flag = true;


    private Authentication() {
        Log.d(TAG, "initialize");
        auth = FirebaseAuth.getInstance();

        EventBus.register(this);

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "onAuthStateChanged: " + user);

                if (user != null) {
                    if (!flag) return;

                    flag = false;
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail() + ":" + user.getDisplayName());
                    User.setCurrent(new User(user));
                    Database.getInstance().initListeners(true);
                    EventBus.post(new UserSignInEvent(User.getCurrent()));
                } else {
                    if (flag && initialized) return;

                    flag = true;
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    EventBus.post(new UserSignOutEvent());
                    Database.getInstance().stopListeners();
                    User.setCurrent(null);
                }

                initialized = true;
            }
        };
    }

    public static Authentication getInstance() {
        return instance;
    }

    public User getCurrentUser() {
        return new User(user);
    }

    public void start() {
        Log.d(TAG, "start");
        auth.addAuthStateListener(authListener);
    }

    public void stop() {
        Log.d(TAG, "stop");
        auth.removeAuthStateListener(authListener);
    }

    public void signIn(String mail, String password, final Context context) {
        auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            EventBus.post(new UserSignInFailedEvent(((FirebaseAuthException) task.getException()).getErrorCode()));
                        }

                        //getCurrentUser().updatePublicKey(context).commit();
                    }
                });
    }

    public void signOut() {
        auth.signOut();
    }

    public void register(final String username, String mail, String password, final Context context) {
        auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "register:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            getCurrentUser().updatePublicKey(context).updateUsername(username).commit();
                        } else {
                            EventBus.post(new UserRegistrationFailedEvent(((FirebaseAuthException) task.getException()).getErrorCode()));
                        }
                    }
                });
    }

    public void reauthenticate(String password, OnCompleteListener<Void> listener) {
        AuthCredential credential = EmailAuthProvider.getCredential(User.getCurrent().getMail(), password);

        user.reauthenticate(credential).addOnCompleteListener(listener);
    }

    @Subscribe
    public void onApplicationEnterForegroundEvent(ApplicationEnterForegroundEvent event) {
        start();
    }

    @Subscribe
    public void onApplicationEnterBackgroundEvent(ApplicationEnterBackgroundEvent event) {
        stop();
    }
}
