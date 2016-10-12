package com.totris.zebra.Utils;


import com.squareup.otto.Bus;

public class EventBus {
    private final static Bus bus = new Bus();

    private EventBus() {

    }

    public static void post(Object event) {
        bus.post(event);
    }

    public static void register(Object object) {
        bus.register(object);
    }

    public static void unregister(Object object) {
        bus.unregister(object);
    }

}
