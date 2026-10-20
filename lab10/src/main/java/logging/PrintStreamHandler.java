package logging;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class PrintStreamHandler extends Handler {

    private final PrintStream stream;

    public PrintStreamHandler(PrintStream stream) {
        this.stream = stream;
    }

    @Override
    public void publish(LogRecord record) {
        if (isLoggable(record)) {
            stream.print(getFormatter().format(record));
            stream.flush();
        }
    }

    @Override
    public void flush() {
        stream.flush();
    }

    @Override
    public void close() {
        stream.flush();
    }
}
