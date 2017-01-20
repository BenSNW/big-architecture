package hx.jdk.forkjoin;

import java.util.concurrent.ForkJoinPool;

public class LongSumDemo {

    static final int NCPU = Runtime.getRuntime().availableProcessors();
    /**
     * for time conversion
     */
    static final long NPS = (1000L * 1000 * 1000);

    static long calcSum;

    static final boolean reportSteals = true;

    public static void main(String[] args) throws Exception {
        int n = 1 << 20;
        int reps = 1 << 8;
        int tests = 2;
        long[] array = new long[n];
        long last, now;
        double elapsed;

        seqFill(array);
        calcSum = seqSum(array);
        System.out.println("seq sum=" + calcSum);

        last = System.nanoTime();

        for (int k = 0; k < reps; ++k) {
            seqSum(array);
        }
        now = System.nanoTime();
        elapsed = (double) (now - last) / (NPS);
        System.out.printf("seq  :  %9.5f\n", elapsed);

        ForkJoinPool fjp;

        for (int i = 2, max = NCPU * 2 + 1; i < max; i += 2) {

            fjp = new ForkJoinPool(i); // with number of threads to use

            for (int j = 0; j < 2; j++) {

                oneRun(fjp, array, i, reps, tests);
            }

            fjp.shutdown();
        }
    }

    static void oneRun(ForkJoinPool fjp,
                       long[] array, int nthreads,
                       int reps, int tests) throws Exception {

//        ParallelLongArray pa = ParallelLongArray.createUsingHandoff(array, fjp);
//        long last, now;
//        long steals = fjp.getStealCount();
//
//        for (int j = 0; j < tests; ++j) {
//
//            last = System.nanoTime();
//            for (int k = 0; k < reps; ++k) {
//
//                long sum = pa.sum();
//
//                if (sum != calcSum) {
//
//                    System.out.println("pa.sum() value=" + sum + " does not match seq sum=" + calcSum);
//                    System.exit(1);
//                }
//            }
//            now = System.nanoTime();
//            double elapsed = (double) (now - last) / (NPS);
//            System.out.printf("ps %2d:  %9.5f", nthreads, elapsed);
//
//            if (reportSteals) {
//                long sc = fjp.getStealCount();
//                long scount = (sc - steals) / reps;
//                steals = sc;
//                System.out.printf(" Steals:%6d", scount);
//            }
//            System.out.println();
//        }
    }

    static void seqFill(long[] array) {
        for (int i = 0; i < array.length; ++i)
            array[i] = 1;
    }

    static long seqSum(long[] array) {
        long sum = 0;
        for (int i = 0; i < array.length; ++i)
            sum += array[i];
        return sum;
    }
}
