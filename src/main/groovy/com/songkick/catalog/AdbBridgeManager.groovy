package com.songkick.catalog

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.logcat.LogCatReceiverTask
import org.gradle.api.GradleException
import org.slf4j.LoggerFactory

class AdbBridgeManager implements Closeable, AndroidDebugBridge.IDeviceChangeListener {

    private static final LOGGER = LoggerFactory.getLogger(AdbBridgeManager.class)
    private static final int MAX_INIT_ATTEMPTS = 100
    private static final int WAIT_BEFORE_NEXT_INIT_ATTEMPT_MS = 50

    private File adbExe
    private AndroidDebugBridge adb
    private List<Device> devices = new ArrayList<>()

    @Override
    public void close() {
        if (adb != null) {
            AndroidDebugBridge.removeDeviceChangeListener(this);
            AndroidDebugBridge.terminate();
            adb = null
        }
    }

    @Override
    void deviceConnected(IDevice device) {
        LOGGER.info("Device connected: ${device}")
    }

    @Override
    void deviceDisconnected(IDevice device) {
        LOGGER.info("Device disconnected: ${device}")
    }

    @Override
    void deviceChanged(IDevice device, int changeMask) {
        LOGGER.debug("Device changed: ${device} (changeMask: ${changeMask})")
    }

    void initializeAdbExe(File adbExeFile) {
        if (adbExe == null) {
            LOGGER.debug("Initialized ADB executable to: ${adbExeFile}")
            adbExe = adbExeFile;
        }
    }

    LogCatRecorderGroup createRecorderGroup() {
        def recorderGroup = new LogCatRecorderGroup({
            if (adb == null) {
                establishBridge()
                addShutdownHook {
                    close()
                }
            }
            devices
        });
        return recorderGroup
    }

    private void establishBridge() {
        LOGGER.info("Establishing ADB bridge")
        if (adbExe == null) {
            throw new GradleException("ADB executable location has not been initialized")
        }
        AndroidDebugBridge.initIfNeeded(false);
        adb = AndroidDebugBridge.createBridge(adbExe.getAbsolutePath(), false);
        if (adb == null) {
            throw new GradleException("Failed to obtain ADB bridge");
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
            if (!adb.hasInitialDeviceList()) {
                throw new GradleException("Unable to establish ADB bridge")
            }
        }
    }


    private void runLogCat() {
        adb.devices.each { device ->
            def receiverTask = new LogCatReceiverTask(device)
            devices.add(new Device(device.name, receiverTask))
            Thread.start { receiverTask.run() }
        }
    }

}
