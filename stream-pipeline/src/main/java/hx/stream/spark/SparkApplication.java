package hx.stream.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparkApplication {

	private static final Logger logger = LoggerFactory.getLogger(SparkApplication.class);

    public static void main(String[] args) {
		logger.info("starting spark-streaming-kafka");
		
		SparkConf sparkConf = new SparkConf(true).setMaster("local").setAppName("spark-demo");
		SparkContext sc = new SparkContext(sparkConf);		

		String filePath = "/usr/local/Cellar/apache-spark/1.6.1/README.md";
		logger.info("readme lines: " + sc.textFile("file://" + filePath, 2).count());
	}
	
}
