package it.unibo.oop.lab.workers02;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class MultiThreadedSumMatrix  implements SumMatrix{
    
    private final int nthread;
    
    public MultiThreadedSumMatrix(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Requests positive integer.");
        }
        this.nthread = n;        
    }
    
    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startPos;
        private final int nElem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param matrix
         *            the matrix to sum
         * @param start
         *            the initial position for this worker
         * @param size
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int start, final int size) {
            super();
            this.matrix = matrix;
            this.startPos = start;
            this.nElem = size;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startPos + " to position " + (startPos + nElem - 1));
            
            res = Stream.of(matrix)
                  .flatMapToDouble(row -> DoubleStream.of(row))
                  .skip(startPos)
                  .limit(nElem).sum();
        }

        /**
         * 
         * @return the sum of every element in the matrix
         */
        public double getResult() {
            return this.res;
        }

    }

    public double sum(final double[][] matrix) {
        final int size = (matrix[0].length * matrix.length) % nthread
                + (matrix[0].length * matrix.length) / nthread;
        
        System.out.println("Size: " + size);
        
        final var res = DoubleStream.iterate(0, start -> start + size)
                           .limit(nthread)
                           .mapToObj(start -> new Worker(matrix, (int)start, size))
                           // start threads
                           .peek(Thread::start)
                           // join threads
                           .peek(MultiThreadedSumMatrix::joinUninterruptibly)
                           //get result of each thread
                           .mapToDouble(Worker::getResult)
                           .sum();
        System.out.println("Sum is: " + res);
        return res;
    }
    
    private static void joinUninterruptibly(final Thread target) {
        boolean joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
