package com.totris.zebra.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;
import com.totris.zebra.Fragments.LoginFragment;
import com.totris.zebra.Fragments.RegisterFragment;
import com.totris.zebra.R;
import com.totris.zebra.Utils.Authentication;

public class MainActivity extends AppCompatActivity implements Authentication.AuthenticationListener, LoginFragment.LoginListener, RegisterFragment.RegisterListener {
    private Authentication auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = Authentication.getInstance();
        auth.setListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_main, new LoginFragment())
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.stop();
    }

    @Override
    public void onUserSignedIn(FirebaseUser user) {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUserSignedOut() {

    }

    @Override
    public void onLogin(String mail, String password) {
        auth.signIn(mail, password);
    }

    @Override
    public void onGotoRegister() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main, new RegisterFragment())
                .commit();
    }

    @Override
    public void onRegister(String username, String mail, String password) {
        auth.register(username, mail, password);
    }

    @Override
    public void onGotoLogin() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main, new LoginFragment())
                .commit();
    }
}
