package com.songkick.catalog
import com.android.ddmlib.logcat.LogCatMessage

import java.awt.Color
import java.util.regex.Matcher
import java.util.regex.Pattern

class LogCatPrinter {
    private static final String TEST_RUNNER = "TestRunner"
    private static final Pattern MESSAGE_START = Pattern.compile("started: ([^(]+)\\(([^)]+)\\)")

    private PrintWriter txtPrintWriter
    private PrintWriter htmlPrintWriter
    private Map<String, String> colorCache

    LogCatPrinter(File txtFile, File htmlFile) {
        this.txtPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(txtFile, false), 16 * 1024))
        this.htmlPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(htmlFile, false), 16 * 1024))
        this.colorCache = new HashMap<>()
    }

    void print(List<LogCatMessage> logCatMessages) {
        htmlPrintWriter.println("<!DOCTYPE html>")
        htmlPrintWriter.println("<html lang=\"en\">")
        htmlPrintWriter.println("<head>")
        htmlPrintWriter.println("<link href=\"logcat.css\" media=\"all\" rel=\"stylesheet\"/>")
        htmlPrintWriter.println("</head>")
        htmlPrintWriter.println("<body>")

        htmlPrintWriter.println("<div class=\"links-container\">")
        for (LogCatMessage logCatMessage : logCatMessages) {
            Matcher match = MESSAGE_START.matcher(logCatMessage.message)
            if (match.matches() && TEST_RUNNER.equals(logCatMessage.tag)) {
                def testName = match.group(1)
                def className = match.group(2)
                def classSimpleName = className.split("\\.").last()
                htmlPrintWriter.println("<a class=\"link\" href=\"#${className}.${testName}\">${classSimpleName} > ${testName}</a>")
            }
        }
        htmlPrintWriter.println("</div>")

        htmlPrintWriter.println("<ul>")

        for (LogCatMessage logCatMessage : logCatMessages) {
            txtPrintWriter.println("${logCatMessage.pid} ${logCatMessage.timestamp} -- ${logCatMessage.message}")

            Matcher match = MESSAGE_START.matcher(logCatMessage.message)
            if (match.matches() && TEST_RUNNER.equals(logCatMessage.tag)) {
                htmlPrintWriter.println("<li class=\"start-container\">")
                def testName = match.group(1)
                def className = match.group(2)
                def classSimpleName = className.split("\\.").last()
                htmlPrintWriter.println("<a href=\"#${className}.${testName}\" id=\"${className}.${testName}\" class=\"start\">${classSimpleName} > ${testName}</a>")
            } else {
                htmlPrintWriter.println("<li>")
                def tagColor = stringToRGB(logCatMessage.tag)
                htmlPrintWriter.println("<div class=\"tag\" style=\"color:${tagColor};\">${logCatMessage.tag}</div>")
                htmlPrintWriter.println("<div class=\"level ${logCatMessage.logLevel.stringValue.toLowerCase(Locale.ROOT)}\">${logCatMessage.logLevel.stringValue[0].toUpperCase(Locale.ROOT)}</div>")
                htmlPrintWriter.println("<div class=\"message\">${logCatMessage.message}</div>")
            }
            htmlPrintWriter.println("</li>")
        }
        htmlPrintWriter.println("</ul>")
        htmlPrintWriter.println("</body>")
        htmlPrintWriter.println("</html>")

        txtPrintWriter.flush()
        txtPrintWriter.close()
        htmlPrintWriter.flush()
        htmlPrintWriter.close()
    }

    String stringToRGB(String s) {
        if (colorCache.containsKey(s)) {
            return colorCache.get(s)
        } else {
            def hexColor = String.format("#%06X", (0xFFFFFF & s.hashCode()))
            def readableRGB = readableRGB(hexColor, 0.5f)
            colorCache.put(s, readableRGB)
            return readableRGB
        }
    }

    static String readableRGB(String hexColor, float amount) {
        Color color = Color.decode(hexColor)
        int red = (int) ((color.red * (1 - amount) / 255 + amount) * 255)
        int green = (int) ((color.green * (1 - amount) / 255 + amount) * 255)
        int blue = (int) ((color.blue * (1 - amount) / 255 + amount) * 255)
        return "rgb($red, $green, $blue)"
    }
}
