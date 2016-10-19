package com.totris.zebra;


import android.util.Log;

import com.orm.SugarApp;
import com.orm.SugarDb;

public class ZebraApplication extends SugarApp {

    private static final String TAG = "ZebraApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

//        // cheating: this forces Sugar to create or upgrade the DB
//        new SugarDb(this).getDB().close();

        initialize();
    }

    protected void initialize() {

    }
}
