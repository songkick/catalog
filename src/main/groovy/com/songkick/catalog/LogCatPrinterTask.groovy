package com.songkick.catalog

import com.android.ddmlib.AndroidDebugBridge
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction


class LogCatPrinterTask extends DefaultTask {

    @Input
    List<Device> devices

    @OutputDirectory
    File outputDir

    @TaskAction
    void run() {
        outputDir.mkdirs()
        devices.each { device ->
            device.task.stop()
            device.task.removeLogCatListener(device.recorder)

            def logcatFileName = "logcat-${device.name.replace(' ', '_')}.txt"
            def logcatFile = new File(outputDir.absolutePath, logcatFileName)
            LogCatPrinter printer = new LogCatPrinter(logcatFile)
            printer.print(device.recorder.recordedMessages)
        }
        devices.clear()

        AndroidDebugBridge.terminate();
    }
}
