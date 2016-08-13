package hx.jdk;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * http://winterbe.com/posts/2014/07/31/java8-stream-tutorial-examples/
 * https://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/
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
				.reduce(0, (sum, i) -> {
					return sum += i;
		}));
		
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
		
	}
}
