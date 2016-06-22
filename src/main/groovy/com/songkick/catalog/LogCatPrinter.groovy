package com.songkick.catalog
import com.android.ddmlib.logcat.LogCatMessage

import java.awt.Color

class LogCatPrinter {

    private PrintWriter txtPrintWriter
    private PrintWriter htmlPrintWriter
    private Map<String, String> colorCache

    LogCatPrinter(File txtFile, File htmlFile) {
        this.txtPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(txtFile, false), 16 * 1024))
        this.htmlPrintWriter = new PrintWriter(new BufferedWriter(new FileWriter(htmlFile, false), 16 * 1024))
        this.colorCache = new HashMap<>()
    }

    void print(Records records) {
        htmlPrintWriter.println("<!DOCTYPE html>")
        htmlPrintWriter.println("<html lang=\"en\">")
        htmlPrintWriter.println("<head>")
        htmlPrintWriter.println("<link href=\"logcat.css\" media=\"all\" rel=\"stylesheet\"/>")
        htmlPrintWriter.println("</head>")
        htmlPrintWriter.println("<body>")

        htmlPrintWriter.println("<div class=\"links-container\">")
        for (int i = 0; i < records.starters.size(); i++) {
            def starter = records.starters.valueAt(i)
            def classSimpleName = starter.className.split("\\.").last()
            htmlPrintWriter.println("<a class=\"link\" href=\"#${starter.className}.${starter.testName}\">${classSimpleName} > ${starter.testName}</a>")
        }
        htmlPrintWriter.println("</div>")

        htmlPrintWriter.println("<ul>")

        records.messages.eachWithIndex { LogCatMessage logCatMessage, i ->
            txtPrintWriter.println("${logCatMessage.pid} ${logCatMessage.timestamp} -- ${logCatMessage.message}")

            if (records.starters.get(i) != null) {
                htmlPrintWriter.println("<li class=\"start-container\">")
                def starter = records.starters.get(i)
                def classSimpleName = starter.className.split("\\.").last()
                htmlPrintWriter.println("<a href=\"#${starter.className}.${starter.testName}\" id=\"${starter.className}.${starter.testName}\" class=\"start\">${classSimpleName} > ${starter.testName}</a>")
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
