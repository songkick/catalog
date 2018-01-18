package com.songkick.catalog

import org.slf4j.LoggerFactory

class RecordedDevice {
    private static final LOGGER = LoggerFactory.getLogger(RecordedDevice.class)

    LogCatRecorder recorder = new LogCatRecorder()

    final Device device

    RecordedDevice(Device device) {
        this.device = device
    }

    void attach() {
        LOGGER.info("Attaching log listener to device: ${device}")
        device.task.addLogCatListener(recorder)
    }

    void close() {
        LOGGER.info("Detaching log listener from device: ${device}")
        device.task.removeLogCatListener(recorder)
    }

}
