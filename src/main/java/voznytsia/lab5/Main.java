package voznytsia.lab5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    private static final String INPUT_FILENAME = "src\\main\\java\\voznytsia\\lab5\\input.txt";
    private static final String OUTPUT_FILENAME = "src\\main\\java\\voznytsia\\lab5\\output.txt";
    private static final Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        Operations operations = new Operations();

        try {
            String inputFilePath = new File(INPUT_FILENAME).getAbsolutePath();
            Scanner scanner = new Scanner(new File(inputFilePath));

            int sizeMM = scanner.nextInt();
            int sizeME = scanner.nextInt();
            int sizeMX = scanner.nextInt();
            int sizeB = scanner.nextInt();
            int sizeD = scanner.nextInt();

            double q = scanner.nextDouble();
            scanner.close();

//            double[][] MM = operations.generateRandomMatrix(sizeMM, sizeMM);
//            double[][] ME = operations.generateRandomMatrix(sizeME, sizeME);
//            double[][] MX = operations.generateRandomMatrix(sizeMX, sizeMX);
//            double[] B = operations.generateRandomArray(sizeB);
//            double[] D = operations.generateRandomArray(sizeD);
            double[][] MM = new double[sizeMM][sizeMM];
            double[][] ME = new double[sizeME][sizeME];
            double[][] MX = new double[sizeMX][sizeMX];
            double[] B = new double[sizeB];
            double[] D = new double[sizeD];

            operations.readMatrix("MM.txt", MM);
            operations.readMatrix("ME.txt", ME);
            operations.readMatrix("MX.txt", MX);
            operations.readVector("B.txt", B);
            operations.readVector("D.txt", D);
//            operations.readMatrix("MMBig.txt", MM);
//            operations.readMatrix("MEBig.txt", ME);
//            operations.readMatrix("MXBig.txt", MX);
//            operations.readVector("BBig.txt", B);
//            operations.readVector("DBig.txt", D);

//            operations.writeArrayToFile(MM, "MM.txt");
//            operations.writeArrayToFile(ME, "ME.txt");
//            operations.writeArrayToFile(MX, "MX.txt");
//            operations.writeArrayToFile(B, "B.txt");
//            operations.writeArrayToFile(D, "D.txt");
//            operations.writeArrayToFile(MM, "MMBig.txt");
//            operations.writeArrayToFile(ME, "MEBig.txt");
//            operations.writeArrayToFile(MX, "MXBig.txt");
//            operations.writeArrayToFile(B, "BBig.txt");
//            operations.writeArrayToFile(D, "DBig.txt");

            String outputFilePath = new File(OUTPUT_FILENAME).getAbsolutePath();
            PrintWriter writer = new PrintWriter(outputFilePath);

            double[][] result1 = new double[sizeMM][sizeMM];
            double[][] result2 = new double[sizeME][sizeME];
            double[] result3 = new double[sizeD];

            CountDownLatch latch = new CountDownLatch(3);
            ExecutorService executor = Executors.newFixedThreadPool(3);

            Callable<double[][]> task1 = () -> {
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
            };

            Callable<double[][]> task2 = () -> {
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
            };

            Callable<double[]> task3 = () -> {
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
            };

            Future<double[][]> future1 = executor.submit(task1);
            Future<double[][]> future2 = executor.submit(task2);
            Future<double[]> future3 = executor.submit(task3);

            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                executor.shutdown();
            }

            double[][] MA = new double[result1.length][result1[0].length];
            for (int i = 0; i < MA.length; i++) {
                for (int j = 0; j < MA[0].length; j++) {
                    MA[i][j] = result1[i][j] + result2[i][j];
                }
            }

            double[] Y = operations.multiplyVectorByMatrix(B, ME);
            for (int i = 0; i < Y.length; i++) {
                Y[i] = Y[i] + result3[i];
            }

            System.out.println("\nFinal Result: \nMA=" + Arrays.deepToString(MA) + "\n\nY=" + Arrays.toString(Y));
            writer.println("\nFinal Result: \nMA=" + Arrays.deepToString(MA) + "\n\nY=" + Arrays.toString(Y));

            long endTime = System.nanoTime();
            long resultTime = (endTime - startTime);
            System.out.println("\nDuration: " + resultTime + " ns");
            writer.println("\nDuration: " + resultTime + " ns");
            writer.close();

//            operations.printMatrix(MM);
//            operations.printMatrix(ME);
//            operations.printMatrix(MX);
//            for (double d : B) {
//                System.out.print(d + " ");
//            }
//            System.out.println("\n");
//            for (double d : D) {
//                System.out.print(d + " ");
//            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
