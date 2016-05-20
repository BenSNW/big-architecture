package hx.dsal;

import java.util.ArrayList;
import java.util.List;

public class FibonacciSeries {

	private static final List<Long> FIBONACCIS = new ArrayList<>(100);
	
	static { FIBONACCIS.add(0L); FIBONACCIS.add(1L); }
	
	public static long naiveFibonacci(int n) {
		if (n < 0)
			throw new IllegalArgumentException();
		if (n < 2)
			return n;
		else
			return naiveFibonacci(n-1) + naiveFibonacci(n-2);
	}
	
	/**
	 * linear algorithm using dynamic programming
	 */
	public static long linearFibonacci(int n) {
		if (n < 0)
			throw new IllegalArgumentException();
		long[] fibbonaccis = new long[n];
		fibbonaccis[0] = 0; fibbonaccis[1] = 1;
		for (int i=2; i<n; i++)
			fibbonaccis[i] = fibbonaccis[i-1] + fibbonaccis[i-2];
		return fibbonaccis[n-1];
	}
	
	/**
	 * unsafe for multi-threaded environment, ConcurrentModificationException may come around
	 */
	public static long get(int n) {
		if (n < 0)
			throw new IllegalArgumentException();
		if (n < FIBONACCIS.size()) 
			return FIBONACCIS.get(n-1);
		for (int i = FIBONACCIS.size(); i<n; i++)
			FIBONACCIS.add(FIBONACCIS.get(i-1) + FIBONACCIS.get(i-2));
		return FIBONACCIS.get(n-1);
	}
	
	public static void main(String[] args) {
		System.out.println(linearFibonacci(10));
		System.out.println(get(100));
		System.out.println(get(30));
	}
}
