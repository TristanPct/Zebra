package com.totris.zebra;


import android.app.Application;

import com.totris.zebra.Models.Message;

public class ZebraApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initialize();
    }

    protected void initialize() {

    }
}
