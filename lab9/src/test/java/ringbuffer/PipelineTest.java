package ringbuffer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PipelineTest {

    private static final String FORMAT =
            "Thread #[67] translated message \"Thread #[1-5] generated message \\d+\"";

    @Test
    @Timeout(20)
    void mainThreadGetsExactlyTheRequestedMessages() throws InterruptedException {
        List<String> messages = PipelineApp.run(100);

        assertEquals(100, messages.size());
        for (String message : messages) {
            assertTrue(message.matches(FORMAT), message);
        }
    }

    @Test
    @Timeout(20)
    void workerThreadsAreDaemons() throws InterruptedException {
        PipelineApp.run(10);

        long workers = Thread.getAllStackTraces().keySet().stream()
                .filter(t -> t.getName().startsWith("Producer-") || t.getName().startsWith("Translator-"))
                .peek(t -> assertTrue(t.isDaemon(), t.getName()))
                .count();
        assertTrue(workers > 0);
    }
}
