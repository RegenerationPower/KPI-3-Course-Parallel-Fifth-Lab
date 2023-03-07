package voznytsia.lab5;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.locks.Lock;

public class Task3 extends RecursiveTask<double[]> {
    private final Operations operations;
    private final double[] D;
    private final double[] B;
    private final double[] result3;
    private final PrintWriter writer;
    private final Lock lock;
    private final CountDownLatch latch;

    public Task3(Operations operations, double[] D, double[] B, double[] result3, PrintWriter writer, Lock lock, CountDownLatch latch) {
        this.operations = operations;
        this.D = D;
        this.B = B;
        this.result3 = result3;
        this.writer = writer;
        this.lock = lock;
        this.latch = latch;
    }

    @Override
    protected double[] compute() {
        double[] r = operations.multiplyVectorByScalar(D, operations.findMinValue(B));
        lock.lock();
        try {
            System.arraycopy(r, 0, result3, 0, r.length);
            System.out.println("\nResult 3: " + Arrays.toString(r));
            writer.println("\nResult 3: " + Arrays.toString(result3));
        } finally {
            lock.unlock();
        }
        latch.countDown();
        return r;
    }

    public static ForkJoinTask<double[]> createTask(Operations operations, double[] D, double[] B, double[] result3, PrintWriter writer, Lock lock, CountDownLatch latch) {
        return new Task3(operations, D, B, result3, writer, lock, latch);
    }
}
