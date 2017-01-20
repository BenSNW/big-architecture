package hx.jdk;

import hx.util.ObjectUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.*;

import static java.util.stream.Collectors.*;

import com.google.common.collect.ImmutableMap;

/**
 * http://www.ibm.com/developerworks/library/j-java-streams-2-brian-goetz/index.html
 * http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/
 * https://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/
 * 
 * http://stackoverflow.com/questions/23038673/merging-two-mapstring-integer-with-java-8-stream-api
 * http://stackoverflow.com/questions/31488339/merging-map-streams-using-java-8-lambda-expression
 * 
 * Created by BenSNW on Jun 25, 2016
 */
public class StreamApi {

	public static void main(String[] args) {
		
		List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd","", "jkl");

		strings.parallelStream().filter(s -> s.isEmpty()).count();
		
		List<String> filtered = strings.stream().filter(s -> !s.isEmpty())
				.collect(Collectors.toList());
		System.out.println("Filtered List: " + filtered);
		
		String mergedString = strings.stream().filter(s -> !s.isEmpty())
				.collect(Collectors.joining(", "));
		System.out.println("Merged String: " + mergedString);
		
		/** reduce
		 * 这个方法的主要作用是把 Stream 元素组合起来。它提供一个起始值（种子），
		 * 然后依照运算规则（BinaryOperator）把 Stream 的第一个、第二个、第 n 个元素组合。
		 * 从这个意义上说，字符串拼接、数值的 sum、min、max、average 都是特殊的 reduce。
		 * 在没有起始值的情况，这时会把 Stream 的前面两个元素组合起来，返回的是 Optional。
		 * 
		 * Optional
		 * 这也是一个模仿 Scala 语言中的概念，作为一个容器，它可能含有某值，或者不包含。
		 * 使用它的目的是尽可能避免 NullPointerException。
		 */
		Optional<String> optionalMerge = strings.stream().reduce(String::concat);
		System.out.println(optionalMerge.get());
		optionalMerge.ifPresent(System.out::println);
		
		System.out.println(
			IntStream.of(1,2,2,4,5)
				.reduce(0, (i, j) -> i + j));
		
		System.out.println(IntStream.of(1,2,2,4,5).reduce(0, Integer::max));
		
		String s = "a b\t\r\n";
		IntStream.range(0, s.length()).forEach(i -> System.out.println((int)s.charAt(i)));
		System.out.println(s.replaceAll("\\s+", "")); // \s -> \t\n\r\f
		System.out.println(Arrays.toString(s.split("，")));
		
		String date = LocalDate.now()
			.plusDays(IntStream.rangeClosed(520, 20000).count())
			.format(DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
		System.out.println(date); 	// 2016年6月24日－2069年10月25日
		
		int sum = IntStream.rangeClosed(520, 20000).reduce(0, Integer::sum);
		System.out.println(sum);	// 199875060
		
		// intermediate operation and terminal operation must be chained together
		// DoubleStream -> DoublePipeline -> AbstractPipeline.linkedOrConsumered
		try {
			DoubleStream stream = new Random().doubles(10);
			stream.peek(System.out::println);
			stream.count();
		} catch (RuntimeException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
		
		// while this is OK
		DoubleStream stream = new Random().doubles(10);
		System.out.println(stream.peek(System.out::println).count());
		
		// notice the execution order
		System.out.println(Arrays.toString(
			Stream.of("one", "two", "three", "four")
			      .filter(e -> e.length() > 3)
			      .peek(e -> System.out.println("Filtered value: " + e))
			      .map(String::toUpperCase)
			      .peek(e -> System.out.println("Mapped value: " + e))
			      .toArray()));
		
		Map<String, Integer> m1 = ImmutableMap.of("a", 2, "b", 3);
	    Map<String, Integer> m2 = ImmutableMap.of("a", 1, "c", 4);
	    System.out.println(mergeMaps(Stream.of(m1, m2)));
	    System.out.println(mergeMaps(Stream.of(m1, m2), Integer::max));

		System.out.println(collectMapValuesAsSet(m1, m2));
		System.out.println(reduceMapValuesAsString(Stream.of(m1, m2)));

		System.out.println(reduceMapValues(Stream.of(m1, m2), i -> i, 0, Integer::sum));
		
	}
	
	public static <K,V> Map<K, String> reduceMapValuesAsString(Stream<? extends Map<K, V>> mapStream) {
		// if mapping is not provided, groupBy returns Map<String, List<Map.Entry>>,
		// so mapping is used to map each Entry to the desired type and then collect them
		return mapStream.flatMap(map -> map.entrySet().stream()).collect(
			groupingBy(Map.Entry::getKey, mapping(entry -> String.valueOf(entry.getValue()), joining(",")))
		);
	}

	public static <K,V> Map<K, V> reduceMapValues(Stream<? extends Map<K, V>> mapStream,
												V identity, BinaryOperator<V> reduceOperator) {
//		return reduceMapValues(mapStream, value -> value, identity, reduceOperator);
		return mapStream.flatMap(map -> map.entrySet().stream()).collect(
			groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, reducing(identity, reduceOperator)))
		);
	}

	// the return type of BinaryOperator is also U since BinaryOperator<T> extends BiFunction<T,T,T>,
	// so U cannot be defined as ? super U otherwise reducing will return Collect<? super U, ? , ? super U>
	public static <K,V,U> Map<K, U> reduceMapValues(Stream<? extends Map<K, V>> mapStream,
													Function<? super V, ? extends U> valueMapper,
													U identity, BinaryOperator<U> reduceOperator) {
		return mapStream.flatMap(map -> map.entrySet().stream()).collect(
					groupingBy( Map.Entry::getKey,
						mapping(entry -> valueMapper.apply(entry.getValue()),
								reducing(identity, reduceOperator)))
		);
	}

	public static <K,V> Map<K, Set<V>> collectMapValuesAsSet(Map<K, V> m1, Map<K, V> m2) {
		return Stream.concat(m1.entrySet().stream(), m2.entrySet().stream()).collect(
					toMap( Map.Entry::getKey,
						entry -> new HashSet<>(Arrays.asList(entry.getValue())),
						(l,r) -> Stream.of(l, r).flatMap(Collection::stream).collect(toSet())
					));
	}

	public static <K,V> Map<K, Set<V>> collectMapValuesAsSet(Stream<? extends Map<K, V>> mapStream) {
//		return collectMapValues(mapStream, v -> v, toSet());
		return mapStream.flatMap(map -> map.entrySet().stream()).collect(
			groupingBy(Map.Entry::getKey, mapping(Map.Entry::getValue, toSet()))
		);
	}

	public static <K,V,U,A> Map<K, A> collectMapValues(Stream<? extends Map<K, V>> mapStream,
													Function<? super V, ? extends U> valueMapper,
													Collector<? super U, ?, A> collector) {
		// if mapping is not provided, groupBy returns Map<String, List<Map.Entry>>,
		// so mapping is used to map each Entry to the desired type and then collect them
		return mapStream.flatMap(map -> map.entrySet().stream()).collect(
					groupingBy( Map.Entry::getKey,
						mapping(entry -> valueMapper.apply(entry.getValue()), collector))
		);
	}

	public static <K,V> Map<K, V> mergeMaps(Stream<? extends Map<K, V>> mapStream) {
		if (ObjectUtil.isEmpty(mapStream))
			return new HashMap<>(0);
		return mapStream.collect(HashMap::new, Map::putAll, Map::putAll);
	}
	
	public static <K,V> Map<K, V> mergeMaps(Stream<? extends Map<K, V>> mapStream, BinaryOperator<V> mergeFunction) {
		if (ObjectUtil.isEmpty(mapStream))
			return new HashMap<>(0);
		BiConsumer<Map<K, V>, Map<K, V>> accumulator = (a, b) -> b.forEach((k,v) -> a.merge(k, v, mergeFunction));
		return mapStream.collect(HashMap::new, accumulator, Map::putAll);
	}
	
}
