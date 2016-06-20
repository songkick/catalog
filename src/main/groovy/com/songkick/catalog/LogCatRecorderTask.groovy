package com.songkick.catalog
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.logcat.LogCatReceiverTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction

class LogCatRecorderTask extends DefaultTask implements AndroidDebugBridge.IDeviceChangeListener {

    private static final int MAX_INIT_ATTEMPTS = 100
    private static final int WAIT_BEFORE_NEXT_INIT_ATTEMPT_MS = 50

    @InputFile
    File adbExe

    private AndroidDebugBridge adb
    private List<Device> devices = new ArrayList<>()

    List<Device> getDevices() {
        return devices
    }

    @TaskAction
    void run() {
        AndroidDebugBridge.initIfNeeded(false);
        adb = AndroidDebugBridge.createBridge(adbExe.getAbsolutePath(), false);
        if (adb == null) {
            throw new IllegalStateException("Failed to create ADB bridge");
        }
        AndroidDebugBridge.addDeviceChangeListener(this);
        if (adb.hasInitialDeviceList()) {
            runLogCat()
        } else {
            int attempts = 0
            while (!adb.hasInitialDeviceList() && attempts < MAX_INIT_ATTEMPTS) {
                Thread.sleep(WAIT_BEFORE_NEXT_INIT_ATTEMPT_MS)
                runLogCat()
                attempts ++
            }
        }
    }

    void runLogCat() {
        adb.devices.each { device ->

            def receiverTask = new LogCatReceiverTask(device)
            def recorder = new LogCatRecorder()
            receiverTask.addLogCatListener(recorder)

            devices.add(new Device(device.name, receiverTask, recorder))

            Thread.start { receiverTask.run() }
        }
    }

    @Override
    void deviceConnected(IDevice device) {
    }

    @Override
    void deviceDisconnected(IDevice device) {
    }

    @Override
    void deviceChanged(IDevice device, int changeMask) {
    }
}
