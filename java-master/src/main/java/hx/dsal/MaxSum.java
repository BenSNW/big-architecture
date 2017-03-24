package hx.dsal;

import java.math.BigDecimal;
import java.util.stream.Stream;
import java.util.stream.DoubleStream;

/**
 * find the contiguous subarray within a 1d array of numbers which has the largest sum
 *
 * @see http://www.cosc.canterbury.ac.nz/tad.takaoka/cats02.pdf
 * @see http://www.geeksforgeeks.org/largest-sum-contiguous-subarray/
 * @see http://www.geeksforgeeks.org/dynamic-programming-set-27-max-sum-rectangle-in-a-2d-matrix/
 *
 * <p>Created by Benchun on 2/22/17
 */
public class MaxSum {

    public static void main(String[] args) {
        System.out.println(maxSum(-2, -3, 4, -1, -2, 1, 5, -3));
        System.out.println(maxSum(-1, -2, -3));

        System.out.println(maxSum2d(new double[][] {{1,2,3}, {4,5}}));
    }

    public static Solution maxSum(double... numbers) {
        Solution solution = new Solution();
        int startReset = 0;
        for (int i = 0; i < numbers.length; i++) {
            solution.currentMax += numbers[i];
            if (solution.currentMax > solution.totalMax) {
                solution.start = startReset;
                solution.end = i + 1;
                solution.totalMax = solution.currentMax;
            }
            if (solution.currentMax < 0) {
                startReset = i + 1;
                solution.currentMax = 0;
            }
        }
        return solution;
    }

    // http://www.geeksforgeeks.org/dynamic-programming-set-27-max-sum-rectangle-in-a-2d-matrix/
    public static double maxSum2d(double[][] numbers) {
        return Stream.of(numbers).parallel().flatMapToDouble(DoubleStream::of).sum();
    }

    private static class Solution {
        int start, end; // dateTime is exclusive
        double currentMax, totalMax;

        @Override
        public String toString() {
            return new BigDecimal(totalMax).stripTrailingZeros().toPlainString() + "(" + start + ", " + end + ")";
        }
    }
}


