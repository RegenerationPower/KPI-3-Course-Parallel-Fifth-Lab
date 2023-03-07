package voznytsia.lab5;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;

class Task2 extends RecursiveTask<double[][]> {
    private final Operations operations;
    private final double[][] ME;
    private final double[][] MX;
    private final double q;
    private final double[][] result2;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;

    Task2(Operations operations, double[][] ME, double[][] MX, double q, double[][] result2, PrintWriter writer, Lock lock, CountDownLatch latch) {
        this.operations = operations;
        this.ME = ME;
        this.MX = MX;
        this.q = q;
        this.result2 = result2;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
    }

    @Override
    protected double[][] compute() {
        double[][] r = operations.multiplyMatrixByScalar(operations.multiplyMatrix(ME, MX), q);
        lock.lock();
        try {
            for (int i = 0; i < r.length; i++) {
                System.arraycopy(r[i], 0, result2[i], 0, r[i].length);
            }
            System.out.println("\nResult 2: " + Arrays.deepToString(r));
            writer.println("\nResult 2: " + Arrays.deepToString(result2));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return r;
    }

    public static ForkJoinTask<double[][]> createTask(Operations operations, double[][] ME, double[][] MX, double q, double[][] result2, PrintWriter writer, Lock lock, CountDownLatch latch) {
        return new Task2(operations, ME, MX, q, result2, writer, lock, latch);
    }
}
