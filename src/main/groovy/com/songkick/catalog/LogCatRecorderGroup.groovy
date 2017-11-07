package com.songkick.catalog

class LogCatRecorderGroup implements Closeable {

    private final List<RecordedDevice> recordedDevices = new ArrayList<>()
    private final Closure<List<Device>> onAttach

	LogCatRecorderGroup(Closure<List<Device>> onAttach) {
        this.onAttach = onAttach
    }

    void attach() {
        def devices = onAttach.call()
        devices.each {
            def recordedDevice = new RecordedDevice(it)
            recordedDevices.add(recordedDevice)
            recordedDevice.attach()
        }
    }

    @Override
    void close() {
        recordedDevices.each { it.close() }
    }

    List<RecordedDevice> getRecordedDevices() {
        return recordedDevices
    }

}
