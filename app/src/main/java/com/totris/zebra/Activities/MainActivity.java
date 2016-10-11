package com.totris.zebra.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;
import com.totris.zebra.R;
import com.totris.zebra.Utils.Authentication;

public class MainActivity extends AppCompatActivity implements Authentication.AuthenticationListener {
    private Authentication auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = Authentication.getInstance();

        auth.setListener(this);
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

    }

    @Override
    public void onUserSignedOut() {

    }
}
