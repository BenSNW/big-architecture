package hx.jdk.forkjoin;

/*
 * 
 * This demo is set up to expose the "compensation threads" problem when
 *   using tiered Phasers with many parties.
 * 
 * The demo is for the Java7 environment. The proposed Fork/Join
 * Classes for Java8 have significant changes, but not such that
 * they no longer pose an excessive thread problem.
 * 
 * If you would like to run with the Java8 Classes then:
 * 
 *   You will need the current source code for jsr166e found at:
 *   http://gee.cs.oswego.edu/dl/concurrency-interest/
 *   
 *   You must add the j.u.c.Phaser class from the Java7 distribution
 *   into the source directory with the package name changed 
 *   to jsr166e since this class is not changing for Java8. You
 *   cannot simply use the jar file from the download.
 *   If you don't, the F/J work threads will stall. 
 *   
 *   Change imports accordingly.
 * 
 *   
 *      ---  options  ---
 *   
 * Change PARTIES to the number of parties for the Phasers. The
 *   more, the more threads the F/J framework will create. When
 *   you get up around 65535, you should run out of memory on 32k
 *   systems.
 *   
 * The System.out.println(); in compute() shows the current
 *   thread name at the waiting stage. If you increase PARTIES
 *   significantly, then comment this line to prevent excessive
 *   printing.
 * 
 */

import java.util.concurrent.Phaser;
//import jsr166e.Phaser;
import java.util.concurrent.ForkJoinPool;
//import jsr166e.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
//import jsr166e.RecursiveAction;

/**
 * Demonstrate trees of Phases are thread hogs,
 * at least when using the Fork/Join framework
 */
public class TieredPhaser {

    // total parties for each phaser
    private static final int PARTIES = 64;

    /**
     * Create a new Phaser with parms and
     * deregister all parent's parties
     *
     * @param parent
     * @param parties
     * @return
     */
    private static Phaser build(Phaser parent, int parties) {

        Phaser next = new Phaser(parent, parties);

        // deregister parent's parties
        for (int i = 0; i < parties; i++)
            parent.arriveAndDeregister();

        return next;
    }

    public static void main(String[] args) {

        System.out.println("Starting");

        ForkJoinPool fjp = new ForkJoinPool();
        Phaser phaser = new Phaser(PARTIES);

        fjp.invoke(new TieredAction(PARTIES, phaser));

        System.out.println("Finished");
    }

    /**
     * Tree of phasers
     */
    private static class TieredAction extends RecursiveAction {

        private final int parties;
        private final Phaser phaser;

        private TieredAction(int parties, Phaser phaser) {
            this.parties = parties;
            this.phaser = phaser;
        }

        @Override
        protected void compute() {

            if (parties == 1) {
      
               /*
                * Shows the thread name at this level
                * For a huge number of parties, comment this line
                */
                System.out.println("Thread waiting at arrive -> " + Thread.currentThread());

                phaser.arriveAndAwaitAdvance();

            } else {
                // half parties in each
                int left = parties / 2;
                int right = parties - left;

                // new phaser in each, except at end
                Phaser leftPhaser = (left > 2) ? build(phaser, left) : phaser;
                Phaser rightPhaser = (right > 2) ? build(phaser, right) : phaser;

                // submit to F/J
                invokeAll(new TieredAction(left, leftPhaser), new TieredAction(right, rightPhaser));
            }
        }
    }
}