package com.songkick.catalog

import com.android.ddmlib.logcat.LogCatReceiverTask;

class Device {
    private String name;
    private LogCatReceiverTask task

    Device(String name, LogCatReceiverTask task) {
        this.name = name
        this.task = task
    }

    String getName() {
        return name
    }

    LogCatReceiverTask getTask() {
        return task
    }

    @Override
    public String toString() {
        return getName()
    }

}