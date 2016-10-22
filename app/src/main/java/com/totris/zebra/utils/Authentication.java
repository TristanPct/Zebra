package com.totris.zebra.utils;


import android.app.Application;
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
import com.totris.zebra.users.User;

import butterknife.OnClick;

public class Authentication {
    private static final String TAG = "Authentication";

    private static Authentication instance = new Authentication();

    private AuthenticationListener listener;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;

    private boolean flag = true;

    private Authentication() {
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (listener != null) {
                    user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        if(!flag) return;

                        flag = false;
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail() + ":" + user.getDisplayName());
                        Database.getInstance().initListeners(true);
                        listener.onUserSignedIn(user);
                    } else {
                        if(flag) return;

                        flag = true;
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        listener.onUserSignedOut();
                        Database.getInstance().stopListeners();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:no_listener");
                }
            }
        };
    }

    public static Authentication getInstance() {
        return instance;
    }

    public User getCurrentUser() {
        return User.from(user);
    }

    public void setListener(AuthenticationListener listener) {
        this.listener = listener;
    }

    public void start() {
        auth.addAuthStateListener(authListener);
    }

    public void stop() {
        auth.removeAuthStateListener(authListener);
    }

    public void signIn(String mail, String password, final Context context) {
        auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful() && listener != null) {
                            listener.onUserSignInFailed(((FirebaseAuthException) task.getException()).getErrorCode());
                            return;
                        }

                        getCurrentUser().updatePublicKey(context).commit();
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
                        } else if (listener != null) {
                            listener.onUserRegistrationFailed(((FirebaseAuthException) task.getException()).getErrorCode());
                        }
                    }
                });
    }

    public void reauthenticate(String password, OnCompleteListener<Void> listener) {
        AuthCredential credential = EmailAuthProvider.getCredential(User.getCurrent().getMail(), password);

        user.reauthenticate(credential).addOnCompleteListener(listener);
    }

    public interface AuthenticationListener {
        void onUserSignedIn(FirebaseUser user);

        void onUserSignedOut();

        void onUserSignInFailed(String message);

        void onUserRegistrationFailed(String message);
    }
}
