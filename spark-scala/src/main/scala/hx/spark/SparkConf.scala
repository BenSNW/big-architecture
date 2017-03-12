package hx.spark

import org.apache.spark.sql.SparkSession

/**
  * Created by Benchun on 3/12/17
  */
object SparkConf {

  implicit private[this] val spark: SparkSession =
    SparkSession.builder().master("local[2]").appName("spark-in-action").getOrCreate()

}
