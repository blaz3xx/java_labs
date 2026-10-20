package ringbuffer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RingBufferTest {

    private static void waitUntilWaiting(Thread thread) throws InterruptedException {
        for (int i = 0; i < 200 && thread.getState() != Thread.State.WAITING; i++) {
            Thread.sleep(10);
        }
        assertEquals(Thread.State.WAITING, thread.getState());
    }

    @Test
    void itemsComeOutInTheSameOrder() throws InterruptedException {
        RingBuffer<Integer> buffer = new RingBuffer<>(5);
        buffer.put(1);
        buffer.put(2);
        buffer.put(3);

        assertEquals(1, buffer.take());
        assertEquals(2, buffer.take());
        assertEquals(3, buffer.take());
    }

    @Test
    void indexesGoRoundTheRing() throws InterruptedException {
        RingBuffer<Integer> buffer = new RingBuffer<>(3);
        for (int i = 0; i < 20; i++) {
            buffer.put(i);
            buffer.put(i + 100);
            assertEquals(i, buffer.take());
            assertEquals(i + 100, buffer.take());
        }
        assertEquals(0, buffer.size());
    }

    @Test
    void capacityAndSizeAreReported() throws InterruptedException {
        RingBuffer<String> buffer = new RingBuffer<>(4);
        buffer.put("a");
        buffer.put("b");

        assertEquals(4, buffer.capacity());
        assertEquals(2, buffer.size());
    }

    @Test
    void capacityMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new RingBuffer<String>(0));
    }

    @Test
    @Timeout(10)
    void putWaitsWhenTheBufferIsFull() throws InterruptedException {
        RingBuffer<Integer> buffer = new RingBuffer<>(2);
        buffer.put(1);
        buffer.put(2);

        Thread producer = new Thread(() -> {
            try {
                buffer.put(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        waitUntilWaiting(producer);
        assertEquals(2, buffer.size());

        assertEquals(1, buffer.take());
        producer.join(2000);

        assertFalse(producer.isAlive());
        assertEquals(2, buffer.size());
    }

    @Test
    @Timeout(10)
    void takeWaitsWhenTheBufferIsEmpty() throws InterruptedException {
        RingBuffer<String> buffer = new RingBuffer<>(2);
        String[] received = new String[1];

        Thread consumer = new Thread(() -> {
            try {
                received[0] = buffer.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        waitUntilWaiting(consumer);

        buffer.put("hello");
        consumer.join(2000);

        assertFalse(consumer.isAlive());
        assertEquals("hello", received[0]);
    }

    @Test
    @Timeout(30)
    void everyItemIsDeliveredExactlyOnce() throws InterruptedException {
        RingBuffer<Integer> buffer = new RingBuffer<>(5);
        int producers = 4;
        int perProducer = 1000;
        int total = producers * perProducer;
        Set<Integer> received = ConcurrentHashMap.newKeySet();
        AtomicInteger remaining = new AtomicInteger(total);
        Thread[] threads = new Thread[producers + 3];

        for (int p = 0; p < producers; p++) {
            int start = p * perProducer;
            threads[p] = new Thread(() -> {
                try {
                    for (int i = 0; i < perProducer; i++) {
                        buffer.put(start + i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        for (int c = 0; c < 3; c++) {
            threads[producers + c] = new Thread(() -> {
                try {
                    while (remaining.getAndDecrement() > 0) {
                        received.add(buffer.take());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(total, received.size());
        assertTrue(received.contains(0) && received.contains(total - 1));
        assertEquals(0, buffer.size());
    }
}
