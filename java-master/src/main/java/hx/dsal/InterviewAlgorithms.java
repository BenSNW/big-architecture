package hx.dsal;

import java.util.Arrays;

public class InterviewAlgorithms {

	/**
	 * linear algorithm to merge two sorted array
	 */
	public static int[] mergeSortedArray(int[] a, int[] b) {
		int[] merge = new int[a.length + b.length];
		int aHead = 0, bHead = 0;
		while (aHead < a.length && bHead < b.length ) 
			merge[aHead + bHead] = a[aHead] < b[bHead] ? a[aHead++] : b[bHead++];	
		while (aHead < a.length)
			merge[aHead + bHead] = a[aHead++];
		while (bHead < b.length)
			merge[aHead + bHead] = b[aHead++];
		return merge;
	}
	
	/**
	 * linear algorithm to merge two sorted array
	 */
	public <T extends Comparable<? super T>> T[] mergeSortedArray(T[] a, T[] b) {
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(mergeSortedArray( new int[] {1, 2, 4}, new int[] {1, 3, 4})));
	}
	
}
