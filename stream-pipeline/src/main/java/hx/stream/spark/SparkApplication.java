package hx.stream.spark;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Tuple2;

public class SparkApplication {

	private static final Logger logger = LoggerFactory.getLogger(SparkApplication.class);

	public static void main(String[] args) {
		logger.info("starting spark-streaming-kafka");
		
		SparkConf sparkConf = new SparkConf(true).setMaster("local[2]").setAppName("spark-demo");
		String filePath = "file:///usr/local/Cellar/apache-spark/1.6.1/README.md";			

		try(JavaSparkContext sc = new JavaSparkContext(sparkConf)) {
			
			JavaRDD<String> linesRDD = sc.textFile(filePath, 2).cache();
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
			});
			
			JavaPairRDD<String, Integer> pairs = words.mapToPair(
				word -> new Tuple2<String, Integer>(word, 1));
			
			JavaPairRDD<String, Integer> wordCount = pairs.reduceByKey(Integer::sum).sortByKey();
			wordCount.collect().forEach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));
			
			wordCount.mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1)).sortByKey(false)
				.foreach(tuple -> System.out.println(tuple._1 + ": " + tuple._2));
			
			wordCount.mapToPair(tuple -> new Tuple2<>(tuple._2, tuple._1)).sortByKey(false)
				.saveAsTextFile("output");
		}

		// SparkSession in Spark 2.0 Spark SQL API
		SparkSession sparkSession = SparkSession.builder()
				.master("local").appName("session-count")
				.getOrCreate();
		System.out.println(sparkSession.sparkContext().master());
		
		Dataset<Row> dataSet = sparkSession.read().text(filePath);
		System.out.println(dataSet.schema());
		dataSet.printSchema();
		System.out.println(Arrays.toString(dataSet.columns()));		
		System.out.println(dataSet.first().mkString());
		
		String jsonFile = "src/main/resources/response.json";
		dataSet = sparkSession.read().json(jsonFile);		
//		sparkSession.sqlContext().jsonFile(jsonFile); // depreciated
		dataSet.printSchema();
		dataSet.show(false);
		
		jsonFile = "src/main/resources/person.json";
		dataSet = sparkSession.read().json(jsonFile);		
//		sparkSession.sqlContext().jsonFile(jsonFile); // depreciated
		dataSet.printSchema();
		dataSet.show(false);
	}
	
}
