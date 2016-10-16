package com.totris.zebra.utils;


import android.util.Log;

import com.squareup.otto.Bus;

public class EventBus {
    private final static Bus bus = new Bus();
    private static final String TAG = "EventBus";

    private EventBus() {

    }

    public static void post(Object event) {
        Log.d(TAG, "post: " + event.toString());
        bus.post(event);
    }

    public static void register(Object object) {
        bus.register(object);
    }

    public static void unregister(Object object) {
        bus.unregister(object);
    }

}
