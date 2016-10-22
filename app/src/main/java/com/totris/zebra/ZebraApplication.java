package com.totris.zebra;


import android.util.Log;

import com.orm.SugarApp;
import com.orm.SugarDb;
import com.totris.zebra.utils.Authentication;
import com.totris.zebra.utils.Database;

public class ZebraApplication extends SugarApp {

    private static final String TAG = "ZebraApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        Authentication.getInstance();
        Database.getInstance();
    }
}
