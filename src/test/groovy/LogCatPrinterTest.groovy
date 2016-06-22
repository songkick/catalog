import com.android.ddmlib.Log
import com.android.ddmlib.logcat.LogCatMessage
import com.android.ddmlib.logcat.LogCatTimestamp
import com.android.utils.SparseArray
import com.google.common.collect.Lists
import com.songkick.catalog.LogCatPrinter
import com.songkick.catalog.Records
import com.songkick.catalog.Starter
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.google.common.truth.Truth.assertThat

class LogCatPrinterTest {

    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    File txtFile, htmlFile;
    LogCatPrinter printer;

    @Before
    public void setUp() throws Exception {
        txtFile = tmpFolder.newFile()
        htmlFile = tmpFolder.newFile()
        printer = new LogCatPrinter(txtFile, htmlFile)
    }

    static def newMessage(String tag, String message) {
        def date = DateTime.now()
        def dateString = date.toString("MM-dd HH:MM:ss.mmm")
        new LogCatMessage(Log.LogLevel.INFO, 123, 0, "MyApp", tag, LogCatTimestamp.fromString(dateString), message)
    }

    @Test
    public void print_emptyList_shouldNotPrintAnything() throws Exception {
        def records = new Records(new ArrayList<LogCatMessage>(), new SparseArray<Starter>())

        printer.print(records)

        assertThat(txtFile.text).isEmpty()
        assertThat(htmlFile.text).isEqualTo('<!DOCTYPE html>\n' +
                '<html lang="en">\n' +
                '<head>\n' +
                '<link href="logcat.css" media="all" rel="stylesheet"/>\n' +
                '</head>\n' +
                '<body>\n' +
                '<div class="links-container">\n' +
                '</div>\n' +
                '<ul>\n' +
                '</ul>\n' +
                '</body>\n' +
                '</html>\n')
    }

    @Test
    public void print_starter_shouldPrintHeader() {
        def startMessage = newMessage("TestRunner", "started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)")
        def messages = Lists.newArrayList(startMessage)
        def starters = new SparseArray()
        starters.put(0, new Starter("com.example.romainpiel.myapplication.MainActivityTest", "dumbTest"))
        def records = new Records(messages, starters)

        printer.print(records)

        assertThat(txtFile.text).contains("-- started: dumbTest(com.example.romainpiel.myapplication.MainActivityTest)\n")
        assertThat(htmlFile.text).isEqualTo('<!DOCTYPE html>\n' +
                '<html lang="en">\n' +
                '<head>\n' +
                '<link href="logcat.css" media="all" rel="stylesheet"/>\n' +
                '</head>\n' +
                '<body>\n' +
                '<div class="links-container">\n' +
                '<a class="link" href="#com.example.romainpiel.myapplication.MainActivityTest.dumbTest">MainActivityTest > dumbTest</a>\n' +
                '</div>\n' +
                '<ul>\n' +
                '<li class="start-container">\n' +
                '<a href="#com.example.romainpiel.myapplication.MainActivityTest.dumbTest" id="com.example.romainpiel.myapplication.MainActivityTest.dumbTest" class="start">MainActivityTest > dumbTest</a>\n' +
                '</li>\n' +
                '</ul>\n' +
                '</body>\n' +
                '</html>\n')
    }

    @Test
    public void print_nonStarter_shouldPrintLine() {
        def message = newMessage("Cupcake", "I'm the sweetest!")
        def messages = Lists.newArrayList(message)
        def starters = new SparseArray()
        def records = new Records(messages, starters)

        printer.print(records)

        assertThat(txtFile.text).contains("-- I'm the sweetest!\n")
        assertThat(htmlFile.text).isEqualTo('<!DOCTYPE html>\n' +
                '<html lang="en">\n' +
                '<head>\n' +
                '<link href="logcat.css" media="all" rel="stylesheet"/>\n' +
                '</head>\n' +
                '<body>\n' +
                '<div class="links-container">\n' +
                '</div>\n' +
                '<ul>\n' +
                '<li>\n' +
                '<div class="tag" style="color:rgb(160, 162, 170);">Cupcake</div>\n' +
                '<div class="level info">I</div>\n' +
                '<div class="message">I\'m the sweetest!</div>\n' +
                '</li>\n' +
                '</ul>\n' +
                '</body>\n' +
                '</html>\n')
    }
}
