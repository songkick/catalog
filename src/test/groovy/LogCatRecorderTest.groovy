import com.android.ddmlib.Log
import com.android.ddmlib.logcat.LogCatMessage
import com.android.ddmlib.logcat.LogCatTimestamp
import com.google.common.collect.Lists
import com.songkick.catalog.LogCatRecorder
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class LogCatRecorderTest {

    static final int DEFAULT_PID = 12345678

    LogCatRecorder recorder

    @Before
    public void setUp() throws Exception {
        recorder = new LogCatRecorder()
    }

    static def newMessage(String tag, String message, int pid = DEFAULT_PID) {
        def date = DateTime.now()
        def dateString = date.toString("MM-dd HH:MM:ss.mmm")
        new LogCatMessage(Log.LogLevel.INFO, pid, 0, "MyApp", tag, LogCatTimestamp.fromString(dateString), message)
    }

    @Test
    public void log_shouldNotRecordBeforeTestRunner() {
        def message = newMessage("Romain", "Bonjour, comment vas-tu?")
        List<LogCatMessage> messages = Lists.newArrayList(message)

        recorder.log(messages)

        def recordedMessages = recorder.records.messages
        def starters = recorder.records.starters
        assertThat(recordedMessages).isEmpty()
        assertThat(starters.size()).isEqualTo(0)
    }

    @Test
    public void log_shouldRecordSamePidAfterStart() {
        def start = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        def message = newMessage("Romain", "Bonjour, comment vas-tu?", 1)
        List<LogCatMessage> messages = Lists.newArrayList(start, message)

        recorder.log(messages)

        def recordedMessages = recorder.records.messages
        assertThat(recordedMessages).containsExactly(start, message)
    }

    @Test
    public void log_shouldNotRecordDifferentPidAfterStart() {
        def start = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        def message = newMessage("Romain", "Bonjour, comment vas-tu?", 2)
        List<LogCatMessage> messages = Lists.newArrayList(start, message)

        recorder.log(messages)

        def recordedMessages = recorder.records.messages
        assertThat(recordedMessages).containsExactly(start)
    }

    @Test
    public void log_shouldRecordOnlyLastTestRunnerPid() {
        def startTestPid1 = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        def endTestPid1 = newMessage("TestRunner", "finished: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)", 1)
        def startTestPid2 = newMessage("TestRunner", "started: dumbTest(com.bakery.CupcakeActivityTest)", 2)
        List<LogCatMessage> messages = Lists.newArrayList(startTestPid1, endTestPid1, startTestPid2)

        recorder.log(messages)

        def recordedMessages = recorder.records.messages
        assertThat(recordedMessages).containsExactly(startTestPid2)
    }

    @Test
    public void log_shouldRecordOneStarter() {
        def message = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        List<LogCatMessage> messages = Lists.newArrayList(message)

        recorder.log(messages)

        def starters = recorder.records.starters
        assertThat(starters.size()).isEqualTo(1)
        def starter = starters.get(0)
        assertThat(starter.className).isEqualTo("com.example.romainpiel.myapplication.MainActivityTest")
        assertThat(starter.testName).isEqualTo("dumbTest")
    }

    @Test
    public void log_shouldRecordTwoStarters() {
        def startTest1 = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        def endTest1 = newMessage("TestRunner", "finished: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        def startTest2 = newMessage("TestRunner", "started: anotherTest(com.example.romainpiel.myapplication.MainActivityTest)")
        List<LogCatMessage> messages = Lists.newArrayList(startTest1, endTest1, startTest2)

        recorder.log(messages)

        def starters = recorder.records.starters
        assertThat(starters.size()).isEqualTo(2)
        def starter1 = starters.get(0)
        assertThat(starter1.className).isEqualTo("com.example.romainpiel.myapplication.MainActivityTest")
        assertThat(starter1.testName).isEqualTo("dumbTest")
        def starter2 = starters.get(2)
        assertThat(starter2.className).isEqualTo("com.example.romainpiel.myapplication.MainActivityTest")
        assertThat(starter2.testName).isEqualTo("anotherTest")
    }
}
