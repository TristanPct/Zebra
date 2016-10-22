package com.totris.zebra;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.totris.zebra.base.ZebraActivity;
import com.totris.zebra.conversations.ConversationsListActivity;
import com.totris.zebra.groups.Group;
import com.totris.zebra.users.User;
import com.totris.zebra.users.auth.LoginActivity;
import com.totris.zebra.users.events.UserSignInEvent;
import com.totris.zebra.users.events.UserSignOutEvent;
import com.totris.zebra.utils.EventBus;

import java.util.Date;

public class SplashActivity extends ZebraActivity {

    private static final String TAG = "SplashActivity";

    private static final long SPLASH_MIN_TIME_OUT = 1500;

    private boolean timeout = false;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        EventBus.register(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorFullscreenStatusBar));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        long elapsedTime = runTasks();
        long splashTime = (elapsedTime > SPLASH_MIN_TIME_OUT) ? 0 : SPLASH_MIN_TIME_OUT;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                timeout = true;
                endTask();
                finish();
            }
        }, splashTime);
    }

    protected long runTasks() {
        Date start = new Date();


        return new Date().getTime() - start.getTime();
    }

    protected void endTask() {
        if (intent != null && timeout) {
            startActivity(intent);
        }
    }

    @Subscribe
    public void onUserSignInEvent(UserSignInEvent event) {
        User.getAll();
        Group.getAll();

        intent = new Intent(SplashActivity.this, ConversationsListActivity.class);
        endTask();
    }

    @Subscribe
    public void onUserSignOutEvent(UserSignOutEvent event) {
        intent = new Intent(SplashActivity.this, LoginActivity.class);
        endTask();
    }
}
