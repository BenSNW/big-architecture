package hx.jdk.forkjoin;


public class SingleSumLongsDemo {

    static final int NCPU = Runtime.getRuntime().availableProcessors();
    /**
     * for time conversion
     */
    static final long NPS = (1000L * 1000 * 1000);

    static long sum;

//    static InternalServer s;
//    static TymeacInterface ti;
//    static TymeacParm TP;

    public static void main(String[] args) throws Exception {

//        int n = 1 << 20;
//        int reps = 1 << 8;
//        int tests = 2;
//        long[] array = new long[n];
//        long last, now;
//        double elapsed;
//
//        seqFill(array);
//        sum = seqSum(array); // for checking
//
//        System.out.println("seq sum=" + sum);
//
//        last = System.nanoTime();
//
//        for (int k = 0; k < reps; ++k) {
//            seqSum(array);
//        }
//        now = System.nanoTime();
//        elapsed = (double) (now - last) / (NPS);
//        System.out.printf("seq  :  %9.5f\n", elapsed);
//
//        // input object for user work Class
//        DoLongInput inp = new DoLongInput(
//                1, // type 1 = sum, others tbd
//                16384, // threshold to Not spawn new tasks  ***--- adjust this for performance ---***
//                0, // low position (origin)
//                array.length, // hi position (fense)
//                array); // the array
//
//        // Parm for the server
//        TP = new TymeacParm(
//                "com.tymeac.dse.serveruser.DoLongs", // work class name
//                inp, // input object passed to user work class
//                5000L); // max wait time 5 sec
//
//        // passed object when starting server
//        String[] in = {"-no", // pos 0 = do not print messages
//                "-threads", // pos 1 = use threads
//                "4",        // pos 2 = this many threads
//                "-s"        // stand alone mode
//        };
//
//        // run through many possible situations
//        for (int i = 2, max = NCPU * 2 + 1; i < max; i += 2) {
//
//            in[2] = new String("" + i); // arg is number of threads
//
//            s = new InternalServer();
//            ti = s.createServer(in); // start the server with options: nbr of threads [to print msgs]
//
//            // When failed, stop
//            if (ti == null) System.exit(1);
//
//            for (int j = 0; j < 2; j++) {
//
//                oneRun(reps, tests, i);
//            }
//
//            // shut down with force
//            ti.shutRequest(true);
//
//        } // endDate-for
//
//        System.exit(0);

    } // endDate-method

    /**
     * Call the Tymeac Server with a synchronous request.
     * To disable checking the result, comment the statement
     * if  (!checkBack(back)) etc.
     *
     * @param reps
     * @param tests
     * @param nbr_threads
     */
    static void oneRun(int reps, int tests, int nbr_threads) {

//        try {
//            for (int j = 0; j < tests; ++j) {
//
//                long last = System.nanoTime();
//                for (int k = 0; k < reps; ++k) {
//
//                    // call tymeac with a synchronous request
//                    Object[] back = ti.syncRequest(TP);
//
//                    // When any invalid return, bye
//                    if (!checkBack(back)) System.exit(1);
//
//                } // endDate-for
//
//                long now = System.nanoTime();
//                double elapsed = (double) (now - last) / (NPS);
//                System.out.printf("Nbr threads %2d : %9.5f\n", nbr_threads, elapsed);
//
//            } // endDate-for
//
//        } catch (Exception e) {
//
//            System.out.println(e.toString());
//
//            System.exit(1);
//
//        } // endDate-catch

    } // endDate-method

    static void check(long[] array) {
        for (int i = 0; i < array.length; ++i) {
            long sum2 = i + 1;
            if (array[i] != sum2) {
                System.out.println("i: " + i + " sum: " + sum2 + " element:" + array[i]);
                throw new Error();
            }
        }
    }

    /**
     * Check the results of calling the Tymeac Server.
     * The return is an array of objects.
     * 1st object is the result of tymeac processing in the format:
     * Tymeac SR(nnnn))
     * Tymeac is a constant
     * SR is for Sync Request (others are AR and SD for shut down)
     * the nnnn is the return code. See /TymeacImpl.java for non-zero.
     * 2nd is the result of application processing:
     * this should be a Long() with a value that matches what was
     * computed in the sequential processing.
     *
     * @param back
     * @return
     */
    static boolean checkBack(Object[] back) {

        // When no back
        if (back == null) {

            // say no good
            System.out.println("back returned null");
            return false;

        } // endif

        if ((back[0] == null) ||
                (!(back[0] instanceof String))) {

            System.out.println("back[0] not a String");
            return false;

        } // endif

        String r = (String) back[0];

        // When invalid return message (minimum message is Tymeac xx(nnnn))
        // or nnnn != 0000
        if ((r.length() < 14) ||
                (r.substring(10, 14).compareTo("0000") != 0)) {

            System.out.println(r);
            return false;

        } // endif

        // number of objects in array
        int nbr = back.length;

        if (nbr < 2) {

            System.out.println("Only one Object in Object[]");
            return false;

        } // endif

        if (!(back[1] instanceof Long)) {

            System.out.println("Long not second object in Object[]");
            return false;

        } // endif

        long sum1 = ((Long) back[1]).longValue();

        if (sum1 != sum) {

            System.out.println("invalid sum: correct=" + sum + " back=" + sum1);
            return false;

        } // endif

        return true;

    } // endDate-method

    static void seqFill(long[] array) {
        for (int i = 0; i < array.length; ++i)
            array[i] = 1;
    }

    static long seqSum(long[] array) {
        long sum1 = 0;
        for (int i = 0; i < array.length; ++i)
            sum1 += array[i];
        return sum1;
    }
}
