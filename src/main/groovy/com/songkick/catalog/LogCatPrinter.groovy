package com.songkick.catalog

import com.android.ddmlib.logcat.LogCatMessage


class LogCatPrinter {

    private PrintWriter printWriter

    LogCatPrinter(File logCatFile) {
        this.printWriter =  new PrintWriter(new BufferedWriter(new FileWriter(logCatFile, false), 16 * 1024))
    }

    void print(List<LogCatMessage> logCatMessages) {
        for (LogCatMessage logCatMessage : logCatMessages) {
            printWriter.println("${logCatMessage.pid} ${logCatMessage.timestamp} -- ${logCatMessage.message}")
        }
        printWriter.flush()
        printWriter.close()
    }
}
