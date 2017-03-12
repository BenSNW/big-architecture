package hx.spark.jdk.forkjoin;

import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * The class first sums an array sequentially then sums the array using the F/J framework.
 * This proves that for < 100 computational steps, sequential is better.
 * 
 * To prove that for > 100 computational steps, F/J is better, change boolean: extraWork = true;
 *
 */
public class LongSum {
    
  static final int SEQUENTIAL_THRESHOLD = 32768; 
  static final long NPS = (1000L * 1000 * 1000);
  static final boolean extraWork = true; // change to add more than just a sum

    class Sum extends RecursiveTask<Long> {

      int low;
      int high;
      long[] array;
  
      Sum(long[] arr, int lo, int hi) {
          array = arr;
          low   = lo;
          high  = hi;
      }
  
      protected Long compute() {
      
        if(high - low <= SEQUENTIAL_THRESHOLD) {
            
          long sum = 0;
          for(int i=low; i < high; ++i) {            
            sum += array[i];            
            // for non-trivial work
            if  (extraWork) 
              uselessCalc(array[i]);             
          }      
          
          return sum;
            
        } else {           
          int mid = low + (high - low) / 2;
          Sum left  = new Sum(array, low, mid);
          Sum right = new Sum(array, mid, high);
          left.fork();
          long rightAns = right.compute();
          long leftAns  = left.join();
          return leftAns + rightAns;                
        }
      }
  }     
       
  static void randomFill(long[] a) {
    final Random rng = new Random();
    for (int i = 0; i < a.length; ++i)
      a[i] = rng.nextLong();
  }
     
  public static void main(String[] args) throws Exception {
                
    LongSum g = new LongSum();
    g.doWork();             
  }
  
  /**
 * usless computation
 * @param n
 */
static void uselessCalc (long n) {
  
  // for non-trivial work
  double temp = n;
  double x = Math.sqrt(temp);
  double y = Math.tanh(temp);
  double z = Math.sinh(temp);    
  
} // end-method
     
  private void doWork() {
       
    int n = 1 << 20;  // 20= 1m
    long[] a1 = new long[n];
    randomFill(a1);
       
    long sum = 0;
       
    long last = System.nanoTime();
       
    // sequentally sum
    for (int i = 0; i < n; i++) {         
      sum += a1[i]; 
      // for non-trivial work
      if  (extraWork) 
        uselessCalc(a1[i]);      
    }
       
    double back = (double)(System.nanoTime() - last) / NPS;
    
    System.out.println("Array Size= " + n + " Threshold= " + SEQUENTIAL_THRESHOLD);
    System.out.printf("Seq Sum time:  %7.9f\n", back); 
       
    // new pool
    ForkJoinPool fjPool = new ForkJoinPool();
       
    last = System.nanoTime();
       
    // do a single invocation
    long total = fjPool.invoke(new Sum(a1, 0, a1.length));
       
    back = (double)(System.nanoTime() - last) / NPS;
       
    System.out.printf("F/J Sum time:  %7.9f\n", back);  
       
    // When sums don't matchFirst
    if  (sum != total) {
        
       System.out.println("Seq Sum= " + sum + " F/J Sum= " + total);
    }              
  }
}
