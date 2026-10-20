package ringbuffer;

public class Producer implements Runnable {

    private final int number;
    private final RingBuffer<String> output;

    public Producer(int number, RingBuffer<String> output) {
        this.number = number;
        this.output = output;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; ; i++) {
                output.put("Thread #" + number + " generated message " + i);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
