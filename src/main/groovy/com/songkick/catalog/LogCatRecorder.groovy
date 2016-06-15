package com.songkick.catalog

import com.android.ddmlib.logcat.LogCatListener
import com.android.ddmlib.logcat.LogCatMessage

import java.util.regex.Matcher
import java.util.regex.Pattern


class LogCatRecorder implements LogCatListener {

    private static final String TEST_RUNNER = "TestRunner"
    private static final Pattern MESSAGE_START = Pattern.compile("started: ([^(]+)\\(([^)]+)\\)")
    private static final Pattern MESSAGE_END = Pattern.compile("finished: [^(]+\\([^)]+\\)")

    private int lastRecordedPid
    private int pid
    private List<LogCatMessage> recordedMessages

    LogCatRecorder() {
        this.lastRecordedPid = -1
        this.pid = -1
        this.recordedMessages = new ArrayList<>()
    }

    @Override
    void log(List<LogCatMessage> logCatMessages) {
        for (LogCatMessage logCatMessage : logCatMessages) {
            if (pid == -1) {
                Matcher match = MESSAGE_START.matcher(logCatMessage.message)
                if (match.matches() && TEST_RUNNER.equals(logCatMessage.tag)) {
                    pid = logCatMessage.pid

                    if (lastRecordedPid != pid) {
                        recordedMessages.clear()
                    }

                    recordedMessages.add(logCatMessage)
                    lastRecordedPid = logCatMessage.pid
                }
            } else {
                if (pid == logCatMessage.pid) {
                    recordedMessages.add(logCatMessage)
                    lastRecordedPid = logCatMessage.pid
                }

                Matcher match = MESSAGE_END.matcher(logCatMessage.message)
                if (match.matches() && TEST_RUNNER.equals(logCatMessage.tag)) {
                    pid = -1
                }
            }
        }
    }

    List<LogCatMessage> getRecordedMessages() {
        return recordedMessages
    }
}
