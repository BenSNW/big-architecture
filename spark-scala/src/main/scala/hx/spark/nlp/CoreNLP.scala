package hx.spark.nlp

import com.databricks.spark.corenlp.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
  * Created by Benchun on 2/16/17
  */
object CoreNLP extends App {

  val spark = SparkSession.builder().master("local[2]").appName("spark-nlp").getOrCreate()

  val input = spark.createDataFrame(Seq(
    (1, "<xml>Stanford University is located in California. It is a great university.</xml>"),
    (2, "<xml>Zhejiang University is located in Hangzhoou.  It is a good  university.</xml>")
  )).toDF("id", "text")

  val output = input
    .select(cleanxml(col("text")).as('doc))
    .select(explode(ssplit(col("doc"))).as('sen))
    .select(col("sen"), tokenize(col("sen")).as('words), sentiment(col("sen")).as('sentiment))
//    .select(col("sen"), tokenize(col("sen")).as('words), ner(col("sen")).as('nerTags), sentiment(col("sen")).as('sentiment))

  output.show(truncate = false)

//  output.select(ner(col("sen")).as('nerTags)).show(false)

}
