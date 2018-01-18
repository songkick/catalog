package com.songkick.catalog

import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class LogCatPrinterTask extends DefaultTask {

    private volatile LogCatRecorderGroup recorderGroup

    @OutputDirectory
    File outputDir

    void setLogCatRecorderGroup(LogCatRecorderGroup recorderGroup) {
        this.recorderGroup = recorderGroup
    }

    @TaskAction
    void run() {
        outputDir.mkdirs()

        recorderGroup.close()
        recorderGroup.recordedDevices.each { recordedDevice ->
            def cssInStream = getClass().getClassLoader().getResourceAsStream('logcat.css')
            def cssOutStream = new File(outputDir.absolutePath, 'logcat.css').newOutputStream()
            IOUtils.copy(cssInStream, cssOutStream)
            cssInStream.close();
            cssOutStream.close();
            def device = recordedDevice.device
            def txtFileName = "logcat-${device.name.replace(' ', '_')}.txt"
            def txtFile = new File(outputDir.absolutePath, txtFileName)
            def htmlFileName = "logcat-${device.name.replace(' ', '_')}.html"
            def htmlFile = new File(outputDir.absolutePath, htmlFileName)
            LogCatPrinter printer = new LogCatPrinter(txtFile, htmlFile)
            printer.print(recordedDevice.recorder.records)
        }
    }
}
