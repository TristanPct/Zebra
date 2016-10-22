package com.totris.zebra.app;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.orm.SugarApp;
import com.totris.zebra.app.events.ApplicationEnterBackgroundEvent;
import com.totris.zebra.app.events.ApplicationEnterForegroundEvent;
import com.totris.zebra.utils.EventBus;
import com.totris.zebra.utils.RsaCrypto;
import com.totris.zebra.utils.Authentication;
import com.totris.zebra.utils.Database;

import java.util.ArrayList;
import java.util.List;

public class ZebraApplication extends SugarApp implements Application.ActivityLifecycleCallbacks {

    private static final String TAG = "ZebraApplication";

    private List<Activity> runningActivities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        registerActivityLifecycleCallbacks(this);

        RsaCrypto.getInstance().setContext(getApplicationContext());
        Authentication.getInstance();
        Database.getInstance();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        runningActivities.add(activity);

        if (runningActivities.size() == 1) {
            EventBus.post(new ApplicationEnterForegroundEvent());
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        runningActivities.remove(activity);

        if (runningActivities.size() == 0) {
            EventBus.post(new ApplicationEnterBackgroundEvent());
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
