package ringbuffer;

public class RingBuffer<T> {

    private static class Node<T> {
        T value;
        Node<T> next;
    }

    private final int capacity;
    private Node<T> head;
    private Node<T> tail;
    private int size;

    public RingBuffer(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("The capacity must be at least 1");
        }
        this.capacity = capacity;

        Node<T> first = new Node<>();
        Node<T> last = first;
        for (int i = 0; i < capacity; i++) {
            Node<T> node = new Node<>();
            last.next = node;
            last = node;
        }
        last.next = first;

        head = first;
        tail = first;
    }

    public int capacity() {
        return capacity;
    }

    public synchronized int size() {
        return size;
    }

    private boolean isEmpty() {
        return head == tail;
    }

    private boolean isFull() {
        return tail.next == head;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        tail.value = item;
        tail = tail.next;
        size++;
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        T item = head.value;
        head.value = null;
        head = head.next;
        size--;
        notifyAll();
        return item;
    }
}
