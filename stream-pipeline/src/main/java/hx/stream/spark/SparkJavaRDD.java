package hx.stream.spark;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;

import scala.Tuple2;

public class SparkJavaRDD {

	public static void main(String[] args) {
		
		SparkConf sparkConf = new SparkConf(true).setMaster("local[2]").setAppName("spark-demo");
		String filePath = "file:///usr/local/Cellar/apache-spark/1.6.1/README.md";			

		try(JavaSparkContext sc = new JavaSparkContext(sparkConf)) {
			
			// set partitions
			JavaRDD<String> linesRDD = sc.textFile(filePath, 1);
			JavaRDD<String> words = linesRDD.flatMap(					
				new FlatMapFunction<String, String>() {

					private static final long serialVersionUID = 1L;
					
					@Override
					public Iterator<String> call(String line) throws Exception {
						// \W is equivalent to [^\w], and \w is equivalent to [a-zA-Z_0-9]
						return Stream.of(line.split("[_\\W]+")) // "[\\.|,|\"|'|-|/|\\s]+"
								.map(String::toLowerCase)
								.filter(StringUtils::isNotBlank)
								.collect(Collectors.toList())
								.iterator();
					}			
			}).cache();
			
			JavaPairRDD<String, Integer> pairs = words.mapToPair(
				word -> new Tuple2<String, Integer>(word, 1));
			
			JavaPairRDD<String, Integer> wordCount = pairs.reduceByKey(Integer::sum).sortByKey();
			wordCount.foreach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));
			
			wordCount.mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1)).sortByKey(false)
				.foreach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));
			
//			wordCount.mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1)).sortByKey(false)
//				.saveAsTextFile("output");
			
			JavaPairRDD<String, Integer> wordLength = words.distinct().mapToPair(
				word -> new Tuple2<String, Integer>(word, word.length()));
			wordLength.sortByKey().foreach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));
			
			JavaPairRDD<Integer, Integer> lengthCount = words.mapToPair(
				word -> new Tuple2<>(word.length(), 1));
			// this operation will not modify the lengthCount
			lengthCount.reduceByKey(Integer::sum).sortByKey()
				.foreach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));
			lengthCount.reduceByKey(Integer::sum).sortByKey().saveAsTextFile("word-length-count.txt");

		}

	}
}
