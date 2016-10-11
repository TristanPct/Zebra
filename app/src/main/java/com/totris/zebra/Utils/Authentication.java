package com.totris.zebra.Utils;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.totris.zebra.Models.User;

public class Authentication {
    private static final String TAG = "Authentication";

    private static Authentication instance;

    private AuthenticationListener listener;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;

    private Authentication() {
        auth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (listener != null) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getEmail());
                        listener.onUserSignedIn(user);
                    } else {
                        Log.d(TAG, "onAuthStateChanged:signed_out");
                        listener.onUserSignedOut();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged:no_listener");
                }
            }
        };
    }

    public static Authentication getInstance() {
        if (instance == null) {
            instance = new Authentication();
        }

        return instance;
    }

    public User getCurrentUser() {
        return User.from(auth.getCurrentUser());
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

    public void signIn(String mail, String password) {
        auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signIn:onComplete:" + task.isSuccessful());
                    }
                });
    }

    public void register(final String username, String mail, String password) {
        auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "register:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            getCurrentUser().updateUsername(username).commit();
                        }
                    }
                });
    }

    public interface AuthenticationListener {
        void onUserSignedIn(FirebaseUser user);
        void onUserSignedOut();
    }
}
