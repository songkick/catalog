package com.songkick.catalog

import com.android.ddmlib.logcat.LogCatReceiverTask;

class Device {
    private String name;
    private LogCatReceiverTask task
    private LogCatRecorder recorder

    Device(String name, LogCatReceiverTask task, LogCatRecorder recorder) {
        this.name = name
        this.task = task
        this.recorder = recorder
    }

    String getName() {
        return name
    }

    LogCatReceiverTask getTask() {
        return task
    }

    LogCatRecorder getRecorder() {
        return recorder
    }
}