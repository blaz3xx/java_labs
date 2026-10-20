package forkjoin;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

public class ArraySumApp {
    public static final int SIZE = 1_000_000;
    public static final int MAX_VALUE = 100;

    public static int[] createArray(int size, Random random) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(MAX_VALUE + 1);
        }
        return array;
    }

    public static long sequentialSum(int[] array) {
        long sum = 0;
        for (int value : array) {
            sum += value;
        }
        return sum;
    }

    public static long forkJoinSum(int[] array, ForkJoinPool pool) {
        return pool.invoke(new ArraySumTask(array));
    }

    public static void main(String[] args) {
        int[] array = createArray(SIZE, new Random());
        System.out.println("Array created: " + array.length + " elements, values 0.." + MAX_VALUE);
        System.out.println("First 10 elements: " + Arrays.toString(Arrays.copyOf(array, 10)));

        try (ForkJoinPool pool = new ForkJoinPool()) {
            System.out.println("Pool parallelism (threads): " + pool.getParallelism());

            long start = System.nanoTime();
            long forkJoinResult = forkJoinSum(array, pool);
            long forkJoinMicros = (System.nanoTime() - start) / 1_000;

            start = System.nanoTime();
            long sequentialResult = sequentialSum(array);
            long sequentialMicros = (System.nanoTime() - start) / 1_000;

            System.out.println("Sum (ForkJoin, threshold " + ArraySumTask.THRESHOLD + "): "
                    + forkJoinResult + "   [" + forkJoinMicros + " us]");
            System.out.println("Sum (sequential):                "
                    + sequentialResult + "   [" + sequentialMicros + " us]");
            System.out.println(forkJoinResult == sequentialResult
                    ? "Results match."
                    : "ERROR: results differ!");
        }
    }
}
