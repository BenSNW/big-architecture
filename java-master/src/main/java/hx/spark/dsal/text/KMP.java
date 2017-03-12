package hx.spark.dsal.text;

import java.util.Arrays;

/**
 * @see Stanford CS97SI String Algorithms.pdf
 * @see Algorithm Design and Applications Michael T. Goodrich Wiley 2014.pdf
 * @see http://blog.csdn.net/miao0967020148/article/details/49099417
 * 
 * <p>Created by BenSNW on Oct 11, 2016
 */
public class KMP {

	/**
	 * Precomputing the fail functions which map the index of P to the
	 * length of the longest prefix of P that is also suffix of P.
	 * 
	 * If there is a mismatch and we have previously made progress in P,
	 * then we consult the failure function to determine the new index in P
	 * where we need to continue checking P against T.
	 * 
	 * <p>Intuitively, if we find a mismatch upon character P[j+1], the function f(j)
	 * tells us how many of the preceding characters can be reused to restart the pattern.
	 * 
	 * @param text T
	 * @param pattern P
	 * @return 
	 */
	public static int match(String text, String pattern) {
		if (pattern == null || text == null || text.length() < pattern.length())
			return -1;
		if (pattern.length() == 0)
			return 0;	// return 0 for empty pattern as String.indexOf does
		int[] f = computeFailFunctions2(pattern);
		int prefixLength = f[0];
		for (int i = 0; i <= text.length() - pattern.length();) {
			// start from prefixLength to avoid unnecessary comparison
			for (int j = prefixLength; j < pattern.length(); j++) {
				if (text.charAt(i+j) == pattern.charAt(j)) {
					if (j == pattern.length() - 1)
						return i;
				} else {
					prefixLength = j > 0 ? f[j-1] : 0;
					// shift by (char matched) - f(char matched)
					i += j > 0 ? j - f[j-1] : 1;
					break;
				}
			}
		}
		return -1;
	}
	
	/**
	 * look at all the prefixes of P that are suffixes of P(0, i-1),
	 * and find the longest one whose next letter matches Pi
	 */
	private static int[] computeFailFunctions(String pattern) {
		int[] f = new int[pattern.length()];
		int k = f[0], i = 1;	// k: previous valid prefix length
		while (i < pattern.length()) {
			// uses the previous values to efficiently compute new values.
			// k = f(k-1) 
			System.out.println("i: " + i + ", k: " + k);
			if (pattern.charAt(i) == pattern.charAt(k)) {
				f[i] = ++k;
				i++;
			} else if (k > 0)	// recursively find k
				k = f[k-1];
			else				// no prefix found, pi[i]=0
				i++;				
		}
		return f;
	}
	
	// better to understand the implicit recursive process
	private static int[] computeFailFunctions2(String pattern) {
		int[] f = new int[pattern.length()];
		int k = 0;
		for (int i = 1; i < pattern.length(); i++) {
			while (k > 0 && pattern.charAt(i) != pattern.charAt(k))
				k = f[k - 1];
			if (pattern.charAt(i) == pattern.charAt(k))
				f[i] = ++k;
		}
		return f;
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString(computeFailFunctions("abbabcdcbaabbc")));
		System.out.println(Arrays.toString(computeFailFunctions2("abbabcdcbaabbc")));
		System.out.println(match("baabaaa", "aabaaa"));
	}
}
