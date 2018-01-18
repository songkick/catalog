package com.songkick.catalog
import com.android.ddmlib.logcat.LogCatListener
import com.android.ddmlib.logcat.LogCatMessage
import com.android.utils.SparseArray

import java.util.regex.Matcher
import java.util.regex.Pattern

class LogCatRecorder implements LogCatListener {

    private static final String TEST_RUNNER = "TestRunner"
    private static final Pattern MESSAGE_START = Pattern.compile("started: ([^(]+)\\(([^)]+)\\)")
    private static final Pattern MESSAGE_END = Pattern.compile("finished: [^(]+\\([^)]+\\)")

    private int lastRecordedPid
    private int pid
    private List<LogCatMessage> recordedMessages
    private SparseArray<Starter> recordedStarters;

    LogCatRecorder() {
        this.lastRecordedPid = -1
        this.pid = -1
        this.recordedMessages = new ArrayList<>()
        this.recordedStarters = new SparseArray<>()
    }

    @Override
    void log(List<LogCatMessage> logCatMessages) {
        logCatMessages.each { LogCatMessage logCatMessage ->
            if (pid == -1) {
                Matcher match = MESSAGE_START.matcher(logCatMessage.message)
                if (match.matches() && TEST_RUNNER.equals(logCatMessage.tag)) {
                    pid = logCatMessage.pid

                    if (lastRecordedPid != pid) {
                        clearRecordedMessages()
                    }

                    def position = recordedMessages.size()
                    record(logCatMessage)
                    def testName = match.group(1)
                    def className = match.group(2)
                    recordedStarters.put(position, new Starter(className, testName))
                }
            } else {
                if (pid == logCatMessage.pid) {
                    record(logCatMessage)
                }

                Matcher match = MESSAGE_END.matcher(logCatMessage.message)
                if (match.matches() && TEST_RUNNER.equals(logCatMessage.tag)) {
                    pid = -1
                }
            }
        }
    }

    private record(LogCatMessage message) {
        recordedMessages.add(message)
        lastRecordedPid = message.pid
    }

    private clearRecordedMessages() {
        recordedMessages.clear()
        recordedStarters.clear()
    }

    Records getRecords() {
        return new Records(recordedMessages, recordedStarters)
    }
}
