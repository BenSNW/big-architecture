package hx.wang.spark.mllib

import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils

/**
  * @see https://spark.apache.org/docs/latest/mllib-linear-methods.html#implementation-developer
  *
  * <p>Created by Benchun on 2/16/17
  */
object LinearSVMClassifier extends App {

  val sparkConf = new SparkConf(true).setMaster("local[2]").setAppName("spark-svm")
  val sc = new SparkContext(sparkConf)

  // Load training data in LIBSVM format.
  val data = MLUtils.loadLibSVMFile(sc, "spark-scala/data/mllib/sample_libsvm_data.txt")

  FileUtils.deleteDirectory(new File("target/tmp"))
  FileUtils.deleteDirectory(new File("spark-scala/target/tmp"))

  // Split data into training (60%) and test (40%).
  val splits = data.randomSplit(Array(0.6, 0.4), seed = 11L)
  val training = splits(0).cache()
  val test = splits(1)

  // Run training algorithm to build the model
  val numIterations = 100
  val model = SVMWithSGD.train(training, numIterations)

  // Clear the default threshold.
  model.clearThreshold()

  // Compute raw scores on the test set.
  val scoreAndLabels = test.map { point =>
    val score = model.predict(point.features)
    (score, point.label)
  }

  // Get evaluation metrics.
  val metrics = new BinaryClassificationMetrics(scoreAndLabels)
  val auROC = metrics.areaUnderROC()

  println("Area under ROC = " + auROC)

  // Save and load model
  model.save(sc, "target/tmp/scalaSVMWithSGDModel")
  val sameModel = SVMModel.load(sc, "target/tmp/scalaSVMWithSGDModel")

}
