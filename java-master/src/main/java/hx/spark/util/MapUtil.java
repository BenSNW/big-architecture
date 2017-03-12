package hx.spark.util;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Ordering;
import com.google.common.primitives.Doubles;

public class MapUtil {

	public static <X extends Comparable<X>, Y> Y getMapValue(X[] keys, Y[] values, X x) {
		if ( keys.length - values.length < -1 || keys.length - values.length > 1)
			throw new IllegalArgumentException("parameter configuration error: "
					+ "keys " + Arrays.toString(keys)
					+ ", values " + Arrays.toString(values)
					+ "; the length of keys and values array doesn't matchFirst");
		if (!isArraySorted(keys, true)) // Ints.asList
			throw new IllegalArgumentException("map keys must be sorted in ascending order");	
		
		int index = Arrays.binarySearch(keys, x);
		if (keys.length + 1 == values.length)	// piecewise map with two infinite boundaries leaved out
			return index < 0 ? values[-index-1] : values[index+1];
		else if (keys.length == values.length && index >= 0) // one-to-one map
			return values[index];
		else if (keys.length == values.length && index < 0)	 // one-to-one map and no matched key found
			throw new IllegalArgumentException("map cannot find matched key: keys " 
					+ Arrays.toString(keys) + ", values "
					+ Arrays.toString(values) + ", input: " + x);
		else if (x.compareTo(keys[0]) < 0 || x.compareTo(keys[keys.length-1]) >= 0)	 // fully-defined piecewise map
			throw new IllegalArgumentException("parameter out of range: keys "
					+ Arrays.toString(keys) + ", input: " + x);
		else
			return index < 0 ? values[-index-2] : values[index];
	}
	
	public static <Y> Y getMapValue(double[] keys, Y[] values, double x) {
		if ( keys.length - values.length < -1 || keys.length - values.length > 1)
			throw new IllegalArgumentException("parameter configuration error: "
					+ "keys " + Arrays.toString(keys)
					+ ", values " + Arrays.toString(values)
					+ "; the length of keys and values array doesn't matchFirst");
		if (!isArraySorted(Doubles.asList(keys), true)) // Ints.asList
			throw new IllegalArgumentException("map keys must be sorted in ascending order");	
		
		int index = Arrays.binarySearch(keys, x);
		if (keys.length + 1 == values.length)	// piecewise map with two infinite boundaries leaved out
			return index < 0 ? values[-index-1] : values[index+1];
		else if (keys.length == values.length && index >= 0) // one-to-one map
			return values[index];
		else if (keys.length == values.length && index < 0)	 // one-to-one map and no matched key found
			throw new IllegalArgumentException("map cannot find matched key: keys " 
					+ Arrays.toString(keys) + ", values "
					+ Arrays.toString(values) + ", input: " + x);
		else if (x < keys[0] || x >= keys[keys.length-1] )	 // fully-defined piecewise map
			throw new IllegalArgumentException("parameter out of range: keys "
					+ Arrays.toString(keys) + ", input: " + x);
		else
			return index < 0 ? values[-index-2] : values[index];
	}
	
	public static boolean isArraySorted(Comparable<?>[] array, boolean ascend) {
		return ascend ? Ordering.natural().isOrdered(Arrays.asList(array))
				: Ordering.natural().reverse().isOrdered(Arrays.asList(array));
	}
	
	public static boolean isArraySorted(
			Collection<? extends Comparable<?>> collection, boolean ascend) {
		return ascend ? Ordering.natural().isOrdered(collection)
				: Ordering.natural().reverse().isOrdered(collection);
	}
}
