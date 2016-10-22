package com.totris.zebra;


import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.orm.SugarApp;
import com.orm.SugarDb;
import com.totris.zebra.utils.RsaCrypto;
import com.totris.zebra.utils.Authentication;
import com.totris.zebra.utils.Database;

public class ZebraApplication extends SugarApp {

    private static final String TAG = "ZebraApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        //new SugarDb(this).getDB().close();

        RsaCrypto.getInstance().setContext(getApplicationContext());
        Authentication.getInstance();
        Database.getInstance();
    }
}
