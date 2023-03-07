package voznytsia.lab5;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;

class Task1 extends RecursiveTask<double[][]> {
    private final Operations operations;
    private final double[][] MM;
    private final double[][] ME;
    private final double[][] MX;
    private final double[][] result1;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;

    Task1(Operations operations, double[][] MM, double[][] ME, double[][] MX, double[][] result1, PrintWriter writer, Lock lock, CountDownLatch latch) {
        this.operations = operations;
        this.MM = MM;
        this.ME = ME;
        this.MX = MX;
        this.result1 = result1;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
    }

    @Override
    protected double[][] compute() {
        double[][] r = operations.multiplyMatrix(MM, operations.subtractMatrix(ME, MX));
        lock.lock();
        try {
            for (int i = 0; i < r.length; i++) {
                System.arraycopy(r[i], 0, result1[i], 0, r[i].length);
            }
            System.out.println("\nResult 1: " + Arrays.deepToString(r));
            writer.println("\nResult 1: " + Arrays.deepToString(result1));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return r;
    }

    public static ForkJoinTask<double[][]> createTask(Operations operations, double[][] MM, double[][] ME, double[][] MX, double[][] result1, PrintWriter writer, Lock lock, CountDownLatch latch) {
        return new Task1(operations, MM, ME, MX, result1, writer, lock, latch);
    }
}
