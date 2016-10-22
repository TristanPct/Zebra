package com.totris.zebra.base;


import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.totris.zebra.base.events.ApplicationEnterForegroundEvent;
import com.totris.zebra.base.events.ApplicationEnterBackgroundEvent;
import com.totris.zebra.utils.EventBus;

public abstract class ZebraActivity extends AppCompatActivity {

    private static final String TAG = "ZebraActivity";

    private boolean isAppWentToBg;
    private boolean isWindowFocused;

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (isAppWentToBg) {
            isAppWentToBg = false;
            Log.d(TAG, "App is in foreground");
        }

        EventBus.post(new ApplicationEnterForegroundEvent());
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();

        Log.d(TAG, "onStop");

        if (!isWindowFocused) {
            isAppWentToBg = true;
            Log.d(TAG, "App is going to background");
        }

        EventBus.post(new ApplicationEnterBackgroundEvent());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isWindowFocused = hasFocus;

        super.onWindowFocusChanged(hasFocus);
    }
}
