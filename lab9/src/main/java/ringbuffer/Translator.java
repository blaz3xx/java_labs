package ringbuffer;

public class Translator implements Runnable {

    private final int number;
    private final RingBuffer<String> input;
    private final RingBuffer<String> output;

    public Translator(int number, RingBuffer<String> input, RingBuffer<String> output) {
        this.number = number;
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = input.take();
                output.put("Thread #" + number + " translated message \"" + message + "\"");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
