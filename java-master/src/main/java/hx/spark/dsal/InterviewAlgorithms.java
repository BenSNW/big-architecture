package hx.spark.dsal;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Algorithms commonly asked in interviews
 * 
 * Created by BenSNW on Jun 3, 2016
 *
 */
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
	public static <T extends Comparable<? super T>> T[] mergeSortedArray(T[] a, T[] b) {
		return null;
	}
	
	/**
	 * reverse a singly linked list, see also Algorithms project
	 */
	public static <T> void reverseList(final SingleLinkedList<T> list) {
		if(list == null || list.size <= 1)
			return;
		list.reverse();
	}
	
	static class SingleLinkedList<T> {

		private Node<T> root;
		private int size;
		
		SingleLinkedList() {
			root = null;
			size = 0;
		}
		
		void reverse() {
			if (size <= 1)
				return;
			root = reverse(null, root);
		}
		
		Node<T> reverse(Node<T> current, Node<T> next) {
			if (next == null)
				return current;
			Node<T> node = next.next;
			next.next = current;
			return reverse(next, node);
		}
		
		static class Node<T> {
			Node<T> next;
			T data;			
			Node(T data) {
				this.data = data;
				this.next = null;
			}
		}
	}
	
	/**
	 * swap variables without using temporary variables
	 * 
	 * @see http://www.geeksforgeeks.org/swap-three-variables-without-using-temporary-variable/
	 * 
	 * @return a cyclic left-shifted array from original array
	 */
	public static int[] swap(int... vars) {
		if (vars.length < 2)
			return vars;
		vars[0] = IntStream.of(vars).sum();
		for (int i = vars.length -1; i >= 0; i--)
			vars[i] = vars[0] - IntStream.of(vars).skip(1).sum();
		return vars;
	}

	public static double sqrt(double x, double error) {
		System.out.println(Math.sqrt(x));
		double val = x/2;
		while (Math.abs(val * val - x) > error)
			val = 0.5 * ( val + x/val );
		return val;
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(mergeSortedArray( new int[] {1, 2, 4}, new int[] {1, 3, 4})));
		System.out.println(Arrays.toString(swap(1, 2, 3, 4)));
		System.out.println(Arrays.toString(swap()));

		System.out.println(sqrt(2, 0.00001));
	}
	
}
