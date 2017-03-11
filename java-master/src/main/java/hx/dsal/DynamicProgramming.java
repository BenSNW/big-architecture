package hx.dsal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class  DynamicProgramming {

	// Algorithms 6.2 P159
	public static Integer[] longestIncreasingSubsequence(int[] input) {
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] lookup = new ArrayList[input.length];
		int longestPathIndex = 0;
		for (int i=0; i<input.length; i++) {
			ArrayList<Integer> path = new ArrayList<>(i/4 + 1);
			int longestPath = 0;
			for (int j=0; j<i; j++) 
				if (input[i] > input[j] && lookup[j].size() > longestPath) {
					path.clear();
					path.addAll(lookup[j]);
					longestPath = lookup[j].size();
				}
								
			path.add(input[i]);
			lookup[i] = path;
			if (path.size() > lookup[longestPathIndex].size())
				longestPathIndex = i;
		}
		
		for (ArrayList<Integer> path : lookup)
			System.out.println(Arrays.toString(path.toArray()));
		
//		return lookup[longestPathIndex].parallelStream().toArray(Integer[]::new);
		
		return lookup[longestPathIndex].toArray(new Integer[lookup[longestPathIndex].size()]);
	}
	
	// Algorithms 6.3 P161
	public static int editDistance(CharSequence seqA, CharSequence seqB) {
		int[][] distanceLookup = new int[seqA.length()][seqB.length()];
		distanceLookup[0][0] = distance(seqA.charAt(0), seqB.charAt(0));
		for (int i=1; i<seqA.length(); i++)	// the first column
			distanceLookup[i][0] = distanceLookup[i-1][0] + distance(seqA.charAt(i), seqB.charAt(0));
		for (int i=1; i<seqB.length(); i++)	// the first row
			distanceLookup[0][i] = distanceLookup[0][i-1] + distance(seqA.charAt(0), seqB.charAt(i));
		for (int i=1; i<seqA.length(); i++) {
			for (int j=1; j<seqB.length(); j++) {
				distanceLookup[i][j] = tupleMin( 
						1 + distanceLookup[i][j-1],
						1 + distanceLookup[i-1][j],
						distance(seqA.charAt(i), seqB.charAt(j)) + distanceLookup[i-1][j-1]);
			}
		}
		
		for (int[] distances : distanceLookup)
			System.out.println(Arrays.toString(distances));
		
		return distanceLookup[seqA.length()-1][seqB.length()-1];
	}
	
	private static int distance(char a, char b) {
		return a == b ? 0 : 1;
	}
	
	private static int tupleMin(int a, int b, int c) {
		return a < b ? ( a < c ? a : c ) : ( b < c ? b : c);
	}
	
	public static int matrixChainMultiplication(Matrix[] matrixes) {
		if (matrixes == null || matrixes.length == 1)
			throw new IllegalArgumentException();
		for (int i=1; i<matrixes.length; i++)
			if (!matrixes[i-1].multiplyMatch(matrixes[i]))
				throw new IllegalArgumentException(i+"");
		
		int N = matrixes.length;
		List<Integer> lookup = new ArrayList<>( N * (N-1) / 2);
		lookup.add(matrixes[0].multiplyOps(matrixes[1])); // sub(0,1)
		System.out.println("0 1 " + lookup.get(0));
		
		for (int j=2; j<N; j++)  		// sub(i, j): i<j<N
			for (int i=j-1; i>=0; i--) 	// from bottom (larger i) up because they are the sub-problems
				lookup.add( optimalCombination(lookup, matrixes, i, j));
				
		return lookup.get(N * (N-1) / 2 - 1);
	}
	
	/** 
	 * the optimal combination: Min{ sub(i, k) + sub(k+1, j) + M<sub>i-k-j</sub> } in which i <= k < j
	 */
	private static int optimalCombination(List<Integer> lookup, Matrix[] matrixes, int i, int j) {
		System.out.print(i + " " + j);
		assert i >= 0 && i < j;
		if (i+1 == j) {
			System.out.println(" " + matrixes[i].multiplyOps(matrixes[j]));
			return matrixes[i].multiplyOps(matrixes[j]);
		}
			
		int min = Integer.MAX_VALUE;
		for (int k=i; k<j; k++) { 
			int subI_k = i == k ? 0 : lookup.get( k*(k-1)/2 + (k-i) - 1); // sub(i, k) and avoid sub(i, i)
			int subKr_J = k+1 == j ? 0: lookup.get( j*(j-1)/2 + (j-k-1) -1); // sub(k+1, j)
			int M_ikj = matrixes[i].rows * matrixes[k+1].rows * matrixes[j].cols;
			min = Math.min(min, subI_k + subKr_J + M_ikj);
		}
		
		System.out.println(" " + min);
		return min;
	}

	static class Matrix {
		
		final int[][] elements;
		int rows, cols;
		
		Matrix(int cols, int[] e) {
			this.cols = cols;
			rows = e.length / cols + ( e.length % cols == 0 ? 0 : 1 );
			elements = new int[rows][cols];
			for (int i=0; i<rows; i++)
				for (int j=0; j<cols; j++)
					if (i*cols+j < e.length)
						elements[i][j] = e[i*cols+j];
			if (e.length % cols != 0)
				for (int j=e.length % cols; j<cols; j++)
					elements[rows-1][j] = 0;
		}
		
		Matrix(int rows, int cols) {
			this.rows = rows;
			this.cols = cols;
			elements = new int[rows][cols];
		}
		
		int get(int i, int j) {
			return elements[i][j];
		}
		
		boolean multiplyMatch(Matrix m) {
			return cols == m.rows;
		}
		
		int multiplyOps(Matrix m) {
			if (! multiplyMatch(m))
				throw new IllegalArgumentException();
			return rows * cols * m.cols;
		}
		
		void dump() {			
			for (int[] row : elements) {
				for (int element : row)
					System.out.print(element + "\t");
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(longestIncreasingSubsequence(new int[] {5,2,8,6,3,6,9,7})));
		System.out.println(editDistance("exponential", "polynomial"));
				
		new Matrix(3, new int[] {1,2,2,3,4,5,8,8,6,9}).dump();;
		Matrix a = new Matrix(50, 20);
		Matrix b = new Matrix(20, 1);
		Matrix c = new Matrix(1, 10);
		Matrix d = new Matrix(10, 100);
		
		System.out.println(matrixChainMultiplication( new Matrix[] { a, b, c, d } ));
		
	}
}