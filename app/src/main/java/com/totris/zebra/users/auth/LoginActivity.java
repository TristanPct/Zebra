package com.totris.zebra.users.auth;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.totris.zebra.base.ZebraActivity;
import com.totris.zebra.conversations.ConversationsListActivity;
import com.totris.zebra.R;
import com.totris.zebra.users.User;
import com.totris.zebra.users.events.UserRegistrationFailedEvent;
import com.totris.zebra.users.events.UserSignInFailedEvent;
import com.totris.zebra.utils.Authentication;
import com.totris.zebra.utils.EventBus;
import com.totris.zebra.utils.WithErrorView;

public class LoginActivity extends ZebraActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener {
    private static final String TAG = "LoginActivity";

    private Authentication auth;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = Authentication.getInstance();

        EventBus.register(this);

        currentFragment = new LoginFragment();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_main, currentFragment)
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

    @Subscribe
    public void onUserSignInEvent(User user) {
        user.updatePublicKey().commit();
        Intent intent = new Intent(this, ConversationsListActivity.class);
        startActivity(intent);
    }

    @Subscribe
    public void onUserSignInFailedEvent(UserSignInFailedEvent event) {
        ((WithErrorView) currentFragment).setError(event.getMessage());
    }

    @Subscribe
    public void onUserRegistrationFailedEvent(UserRegistrationFailedEvent event) {
        ((WithErrorView) currentFragment).setError(event.getMessage());
    }

//    @Override
//    public void onUserSignedIn(FirebaseUser user) {
//        User.getCurrent().updatePublicKey(getApplicationContext()).commit();
//        Intent intent = new Intent(this, ConversationsListActivity.class);
//        startActivity(intent);
//    }
//
//    @Override
//    public void onUserSignedOut() {
//
//    }
//
//    @Override
//    public void onUserSignInFailed(String error) {
//        ((WithErrorView)currentFragment).setError(error);
//    }
//
//    @Override
//    public void onUserRegistrationFailed(String error) {
//        ((WithErrorView)currentFragment).setError(error);
//    }

    @Override
    public void onLogin(String mail, String password) {
        auth.signIn(mail, password, getApplicationContext());
    }

    @Override
    public void onGotoRegister() {
        currentFragment = new RegisterFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main, currentFragment)
                .commit();
    }

    @Override
    public void onRegister(String username, String mail, String password) {
        auth.register(username, mail, password, getApplicationContext());
    }

    @Override
    public void onGotoLogin() {
        currentFragment = new LoginFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main, currentFragment)
                .commit();
    }
}
