package forkjoin;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArraySumTaskTest {
    @Test
    void createArrayHasRequestedSizeAndRange() {
        int[] array = ArraySumApp.createArray(ArraySumApp.SIZE, new Random(1));

        assertEquals(1_000_000, array.length);
        for (int value : array) {
            assertTrue(value >= 0 && value <= 100, "value out of range 0..100: " + value);
        }
    }

    @Test
    void forkJoinSumEqualsSequentialSumOnMillionElements() {
        int[] array = ArraySumApp.createArray(ArraySumApp.SIZE, new Random(42));

        try (ForkJoinPool pool = new ForkJoinPool(4)) {
            assertEquals(ArraySumApp.sequentialSum(array), ArraySumApp.forkJoinSum(array, pool));
        }
    }

    @Test
    void emptyArrayGivesZero() {
        assertEquals(0L, new ArraySumTask(new int[0]).invoke());
    }

    @Test
    void arraysAroundThresholdAreSummedCorrectly() {
        for (int size : new int[]{1, 19, 20, 21, 39, 40, 41, 1000}) {
            int[] array = new int[size];
            for (int i = 0; i < size; i++) {
                array[i] = i + 1;
            }
            long expected = (long) size * (size + 1) / 2;
            assertEquals(expected, new ArraySumTask(array).invoke(), "size=" + size);
        }
    }

    @Test
    void subRangeIsSummedOnly() {
        int[] array = {100, 1, 2, 3, 100};
        assertEquals(6L, new ArraySumTask(array, 1, 4).invoke());
    }

    @Test
    void worksWithSingleThreadPool() {
        int[] array = ArraySumApp.createArray(10_000, new Random(7));

        try (ForkJoinPool pool = new ForkJoinPool(1)) {
            assertEquals(ArraySumApp.sequentialSum(array), ArraySumApp.forkJoinSum(array, pool));
        }
    }
}
