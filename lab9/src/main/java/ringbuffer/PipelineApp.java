package ringbuffer;

import java.util.ArrayList;
import java.util.List;

public class PipelineApp {

    public static final int PRODUCERS = 5;
    public static final int TRANSLATORS = 2;
    public static final int BUFFER_SIZE = 10;
    public static final int MESSAGES_TO_PRINT = 100;

    private static void startDaemon(String name, Runnable task) {
        Thread thread = new Thread(task, name);
        thread.setDaemon(true);
        thread.start();
    }

    public static List<String> run(int messageCount) throws InterruptedException {
        RingBuffer<String> generated = new RingBuffer<>(BUFFER_SIZE);
        RingBuffer<String> translated = new RingBuffer<>(BUFFER_SIZE);

        for (int i = 1; i <= PRODUCERS; i++) {
            startDaemon("Producer-" + i, new Producer(i, generated));
        }
        for (int i = 1; i <= TRANSLATORS; i++) {
            int number = PRODUCERS + i;
            startDaemon("Translator-" + number, new Translator(number, generated, translated));
        }

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < messageCount; i++) {
            messages.add(translated.take());
        }
        return messages;
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> messages = run(MESSAGES_TO_PRINT);
        for (int i = 0; i < messages.size(); i++) {
            System.out.println((i + 1) + ". " + messages.get(i));
        }
        System.out.println("The main thread printed " + messages.size() + " messages and finished! <3");
    }
}
