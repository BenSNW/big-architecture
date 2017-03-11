package hx.wang.spark.ml

import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.ml.param.ParamMap
import org.apache.spark.sql.{Row, SparkSession}

/**
  * A ML Pipeline chains multiple Transformers and Estimators together to specify an ML workflow.
  * All Transformers and Estimators now share a common Parameter API for specifying parameters.
  *
  * <p>A Transformer is an abstraction that includes feature transformers and learned models.
  * Technically, a Transformer implements a method transform(), which converts one DataFrame into another.
  *
  * <li>A feature transformer might take a DataFrame, read a column (e.g., text), map it into a new
  * column (e.g., feature vectors), and output a new DataFrame with the mapped column appended.
  *
  * <li>A learning model might take a DataFrame, read the column containing feature vectors, predict the
  * label for each feature vector, and output a new DataFrame with predicted labels appended as a column.
  *
  * <p>An Estimator abstracts the concept of a learning algorithm or any algorithm that fits or trains on data.
  * Technically, an Estimator implements a method fit(), which accepts a DataFrame and produces a Model,
  * which is a Transformer. For example, a learning algorithm such as LogisticRegression is an Estimator,
  * and calling fit() trains a LogisticRegressionModel, which is a Model and hence a Transformer.
  *
  * @see https://spark.apache.org/docs/latest/ml-pipeline.html
  *
  * <p>Created by Benchun on 1/20/17
  */
object LRTransformer extends App {

  val spark = SparkSession.builder().master("local[2]").appName("spark-ml").getOrCreate()

  // Prepare training data from a list of (label, features) tuples.
  val training = spark.createDataFrame(Seq(
    (1.0, Vectors.dense(0.0, 1.1, 0.1)),
    (0.0, Vectors.dense(2.0, 1.0, -1.0)),
    (0.0, Vectors.dense(2.0, 1.3, 1.0)),
    (1.0, Vectors.dense(0.0, 1.2, -0.5))
  )).toDF("label", "features")

  // Create a LogisticRegression instance. This instance is an Estimator.
  val lr = new LogisticRegression()
  // Print out the parameters, documentation, and any default values.
  println("LogisticRegression parameters:\n" + lr.explainParams() + "\n")

  // We may set parameters using setter methods.
  lr.setMaxIter(10).setRegParam(0.01)

  // Learn a LogisticRegression model. This uses the parameters stored in lr.
  val model1 = lr.fit(training)
  // Since model1 is a Model (i.e., a Transformer produced by an Estimator),
  // we can view the parameters it used during fit().
  // This prints the parameter (name: value) pairs, where names are unique IDs for this
  // LogisticRegression instance.
  println("Model 1 was fit using parameters: " + model1.parent.extractParamMap)

  // We may alternatively specify parameters using a ParamMap,
  // which supports several methods for specifying parameters.
  val paramMap = ParamMap(lr.maxIter -> 20, lr.regParam -> 0.1, lr.threshold -> 0.55)
//    .put(lr.maxIter, 30)  // Specify 1 Param. This overwrites the original maxIter.
//    .put(lr.regParam -> 0.1, lr.threshold -> 0.55)  // Specify multiple Params.

  // One can also combine ParamMaps.
  val paramMap2 = ParamMap(lr.probabilityCol -> "myProbability")  // Change output column name.
  val paramMapCombined = paramMap ++ paramMap2

  // Now learn a new model using the paramMapCombined parameters.
  // paramMapCombined overrides all parameters set earlier via lr.set* methods.
  val model2 = lr.fit(training, paramMapCombined)
  println("Model 2 was fit using parameters: " + model2.parent.extractParamMap)

  // Prepare test data.
  val test = spark.createDataFrame(Seq(
    (1.0, Vectors.dense(-1.0, 1.5, 1.3)),
    (0.0, Vectors.dense(3.0, 2.0, -0.1)),
    (1.0, Vectors.dense(0.0, 2.2, -1.5))
  )).toDF("label", "features")

  // Make predictions on test data using the Transformer.transform() method.
  // LogisticRegression.transform will only use the 'features' column.
  // Note that model2.transform() outputs a 'myProbability' column instead of the usual
  // 'probability' column since we renamed the lr.probabilityCol parameter previously.
  model2.transform(test)
    .select("features", "label", "myProbability", "prediction")
    .collect()
    .foreach { case Row(features: Vector, label: Double, prob: Vector, prediction: Double) =>
      println(s"($features, $label) -> prob=$prob, prediction=$prediction")
    }
}
