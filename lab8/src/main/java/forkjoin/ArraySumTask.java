package forkjoin;

import java.util.concurrent.RecursiveTask;

public class ArraySumTask extends RecursiveTask<Long> {
    private static final long serialVersionUID = 1L;

    public static final int THRESHOLD = 20;

    private final int[] array;
    private final int from;
    private final int to;

    public ArraySumTask(int[] array, int from, int to) {
        this.array = array;
        this.from = from;
        this.to = to;
    }

    public ArraySumTask(int[] array) {
        this(array, 0, array.length);
    }

    @Override
    protected Long compute() {
        int length = to - from;

        if (length < THRESHOLD) {
            long sum = 0;
            for (int i = from; i < to; i++) {
                sum += array[i];
            }
            return sum;
        }

        int middle = from + length / 2;
        ArraySumTask left = new ArraySumTask(array, from, middle);
        ArraySumTask right = new ArraySumTask(array, middle, to);

        left.fork();
        long rightSum = right.compute();

        long leftSum = left.join();

        return leftSum + rightSum;
    }
}
