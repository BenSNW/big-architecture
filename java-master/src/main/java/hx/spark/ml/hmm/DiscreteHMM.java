package hx.spark.ml.hmm;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @see https://en.wikibooks.org/wiki/Algorithm_Implementation/Viterbi_algorithm
 * @see http://cs.nyu.edu/courses/spring04/G22.2591-001/BW%20demo/HMM.java
 * @see http://12000.org/my_notes/HMM_program/report/report.htm#x1-50001
 *
 * <p>Created by Benchun on 2/18/17
 */
public class DiscreteHMM {

    /**
     * pi: initial hidden state probabilities
     * A: transitions of hidden states, B: emissions, B[i][j] = P(observation j | hidden state i)
     *
     * always use row to represent hidden states and column to represent observed response
     */
    float[]     pi;
    float[][] a, b;

    public DiscreteHMM(int hiddenStates, int observedStates) {
        pi = new float[hiddenStates];
        a  = new float[hiddenStates][hiddenStates];
        b  = new float[hiddenStates][observedStates];
    }

    /**
     * find the probability of an observed sequence using forward algorithm (alpha recursion).
     *
     * time complexity: sequence length N * hidden states K * hidden states K,
     * since K is usually much smaller than N, it grows linearly with N.
     * If using exhaustive search, it takes K to the power of N which grows exponentially with N.
     *
     * @param observations  observed state sequence using 1-of-K encoding (starting from 0)
     * @return
     */
    public float probOfObservedSequence(int... observations) {
        // Stream.of(int[]) will return Stream<int[]> whereas Arrays.stream(int[]) will return IntStream
        if (observations.length <=0 || Arrays.stream(observations).anyMatch(o -> o < 0 || o >= b.length))
            return 0;
        float[] prob = forwardProbabilities(observations)[observations.length-1];
        return (float) IntStream.range(0, observations.length).mapToDouble(i -> prob[i]).reduce(0, Double::sum);
    }

    private float[][] forwardProbabilities(int... observations) {
        // observation probability given every hidden state
        float[][] forwardProb = new float[b.length][observations.length];

        // solve column by column which represent the contribution of observation at time t from each hidden state
        for (int i = 0; i < b.length; i++)
            forwardProb[i][0] = pi[i] * b[i][observations[0]];

        for (int j = 1; j < observations.length; j++) { // for each observed response
            for (int i = 0; i < b.length; i++) {        // for each current hidden state
                for (int k = 0; k < b.length; k++)      // for each previous hidden state
                    forwardProb[i][j] += forwardProb[k][j - 1] * a[k][i];
                forwardProb[i][j] *= b[i][observations[j]];
            }
        }

        return forwardProb;
    }

    public static void main(String[] args) {
        DiscreteHMM hmm = new DiscreteHMM(2, 3);
        hmm.pi[0] = 0.8f;
        hmm.pi[1] = 0.2f;

        hmm.a[0][0] = 0.7f;
        hmm.a[0][1] = 0.3f;
        hmm.a[1][1] = 0.5f;
        hmm.a[1][0] = 0.5f;

        hmm.b[0][0] = 0.6f;
        hmm.b[0][1] = 0.1f;
        hmm.b[0][2] = 0.3f;
        hmm.b[1][0] = 0.1f;
        hmm.b[1][1] = 0.7f;
        hmm.b[1][2] = 0.2f;

        float[][] fwdProb = hmm.forwardProbabilities(0, 0, 0);
        System.out.println(Arrays.deepToString(hmm.forwardProbabilities(0, 0, 0)));
        System.out.println(Arrays.deepToString(hmm.forwardProbabilities(1, 0, 0)));
        System.out.println(Arrays.deepToString(hmm.forwardProbabilities(2, 0, 0)));
    }
}
