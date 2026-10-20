package logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LineFormatter extends Formatter {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final boolean withStackTrace;

    public LineFormatter(boolean withStackTrace) {
        this.withStackTrace = withStackTrace;
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder line = new StringBuilder();
        line.append(TIME.format(LocalDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault())))
                .append(' ')
                .append(String.format("%-7s", record.getLevel().getName()))
                .append(' ')
                .append(shortName(record.getLoggerName()))
                .append(" - ")
                .append(formatMessage(record));

        Throwable thrown = record.getThrown();
        if (thrown != null) {
            if (withStackTrace) {
                StringWriter trace = new StringWriter();
                thrown.printStackTrace(new PrintWriter(trace));
                line.append(System.lineSeparator()).append(trace.toString().stripTrailing());
            } else {
                line.append(": ").append(thrown);
            }
        }
        return line.append(System.lineSeparator()).toString();
    }

    private static String shortName(String loggerName) {
        if (loggerName == null) {
            return "";
        }
        return loggerName.substring(loggerName.lastIndexOf('.') + 1);
    }
}
