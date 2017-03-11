package hx.dsal;

import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * @see http://www.geeksforgeeks.org/find-the-element-that-appears-once/
 * @see http://www.geeksforgeeks.org/swap-three-variables-without-using-temporary-variable/
 * 
 * <p>Created by BenSNW on Oct 11, 2016
 */
public class BitwiseAlgorithm {

    public static void main(String[] args) {
        System.out.println(1>>1);
        System.out.println(1>>>1);

        System.out.println(-1>>1);  // signed bit shift
        System.out.println(-2>>1);
        System.out.println(-1<<1);
        System.out.println(-1>>>1); // unsigned bit shift, highest bit is guaranteed to be 0
        System.out.println(-2>>>1);

        BitSet bits = BitSet.valueOf(new long[] { -1 });
//        bits.stream().forEach(System.out::println);
        System.out.println(bits.get(0) + " " + bits.get(1));

        bits = BitSet.valueOf(new long[] { -2 });
//        bits.stream().forEach(System.out::println);
        System.out.println(bits.get(0) + " " + bits.get(1));

        bits = BitSet.valueOf(ByteBuffer.allocate(4).putInt(-2));
        bits.stream().forEach(System.out::println);
        System.out.println(bits.get(0) + " " + bits.get(1));

        System.out.println(Integer.MAX_VALUE + 1);
        System.out.println(Integer.MAX_VALUE <<1);
        System.out.println(1>>1<<1);
    }

}
