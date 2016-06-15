package com.songkick.catalog

import com.android.ddmlib.logcat.LogCatMessage


class LogCatPrinter {

    private PrintWriter txtPrintWriter
    private PrintWriter htmlPrintWriter

    LogCatPrinter(File txtFile, File htmlFile) {
        this.txtPrintWriter =  new PrintWriter(new BufferedWriter(new FileWriter(txtFile, false), 16 * 1024))
        this.htmlPrintWriter =  new PrintWriter(new BufferedWriter(new FileWriter(htmlFile, false), 16 * 1024))
    }

    void print(List<LogCatMessage> logCatMessages) {
        htmlPrintWriter.println("<!DOCTYPE html>")
        htmlPrintWriter.println("<html lang=\"en\">")
        htmlPrintWriter.println("<head>")
        htmlPrintWriter.println("<link href=\"logcat.css\" media=\"all\" rel=\"stylesheet\"/>")
        htmlPrintWriter.println("</head>")
        htmlPrintWriter.println("<body>")
        htmlPrintWriter.println("<ul>")
        for (LogCatMessage logCatMessage : logCatMessages) {
            txtPrintWriter.println("${logCatMessage.pid} ${logCatMessage.timestamp} -- ${logCatMessage.message}")
            htmlPrintWriter.println("<li>")
            htmlPrintWriter.println("<div class=\"tag\">${logCatMessage.tag}</div>")
            htmlPrintWriter.println("<div class=\"level ${logCatMessage.logLevel.stringValue.toLowerCase(Locale.ROOT)}\">${logCatMessage.logLevel.stringValue[0].toUpperCase(Locale.ROOT)}</div>")
            htmlPrintWriter.println("<div class=\"message\">${logCatMessage.message}</div>")
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
}
