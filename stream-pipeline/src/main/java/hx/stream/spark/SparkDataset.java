package hx.stream.spark;

import java.util.List;
import java.util.stream.Stream;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalog.Catalog;
import org.apache.spark.sql.catalog.Table;
import static org.apache.spark.sql.types.DataTypes.*;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

/**
 * <p>Created by BenSNW on Nov 4, 2016
 * 
 * @see https://ideata-analytics.com/apache-spark-api-comparision/
 */
public class SparkDataset {

	public static void main(String[] args) {
		
		// SparkSession in Spark 2.0 Spark SQL API
		SparkSession spark = SparkSession.builder()
				.master("local[*]").appName("spark-hive")
//				.config("spark.sql.warehouse.dir", "hive-warehouse")
//				.enableHiveSupport()	// require spark-hive support
				.getOrCreate();
		
		System.out.println(spark.sparkContext().master());
		// file:${system:user.dir}/spark-warehouse
		System.out.println(spark.conf().get("spark.sql.warehouse.dir"));
				
		// In the Scala API, DataFrame is simply a type alias of Dataset[Row].
		// While, in Java API, users need to use Dataset<Row> to represent a DataFrame.
		String jsonFile = "src/main/resources/person.json";
		StructType schema = createStructType(
				Stream.of("id", "name", "age")
					.map(field -> createStructField(
							field,
							field.equals("name") ? StringType : IntegerType,
							false))
					.toArray(StructField[]::new)
				);
		Dataset<Row> dataFrame = spark.read().format("json").schema(schema).load(jsonFile);
//		sparkSession.sqlContext().jsonFile(jsonFile); // depreciated
		dataFrame.printSchema();
		dataFrame.show(false);
		
		dataFrame.select("name").show(false);
		dataFrame.describe("id").show(false);

		// Register as a SQL temporary view to run sql statements
		dataFrame.createOrReplaceTempView("person");
		dataFrame.sqlContext().sql("select * from person").show(false);

		/**
		 *  Note that the old SQLContext and HiveContext are kept for backward compatibility.
		 *  A new catalog interface is accessible from SparkSession - existing API on databases
		 *  and tables access such as listTables, createExternalTable, dropTempView, cacheTable
		 *  are moved here.
		 */
		Catalog catalog = dataFrame.sparkSession().catalog();
		System.out.println(catalog.currentDatabase());	// default
		catalog.dropTempView("person");
		
		/**
		 * Datastore: The class "org.apache.hadoop.hive.metastore.model.MOrder" is tagged as
		 * "embedded-only" so does not have its own datastore table.
		 * 
		 * Deleted the diretory file:/Users/Benchun/Documents/Engineering/eclipse/big-architecture/stream-pipeline/spark-warehouse/person
		 */
		Dataset<Table> tables = catalog.listTables();
		tables.collectAsList().forEach(table -> System.out.println(table.name()));
		dataFrame.sqlContext().sql("drop table if exists person");

		/**
		 * Save to persistent Hive table: Spark will create a default local Hive metastore using Derby
		 *
		 * Persisting data source relation `t_person` with a single input path
		 * into Hive metastore in Hive compatible format.
		 * Input path: file:/Users/Benchun/Documents/Engineering/eclipse/big-architecture/stream-pipeline/spark-warehouse/t_person.
		 */

		// org.apache.spark.sql.execution.datasources.parquet.ParquetWriteSupport		
		dataFrame.repartition(1).write().mode(SaveMode.Overwrite).saveAsTable("t_person");
		spark.sql("select * from t_person").show(false);

		// Convert DataFrame to Dataset through a Row mapper
		Dataset<Person> dataSet = dataFrame.map(
				new MapFunction<Row, Person>() {
					private static final long serialVersionUID = 1L;
					public Person call(Row row) throws Exception {
						return new Person(row.getInt(0), row.getString(1), row.getInt(2));
					}
				},
				Encoders.bean(Person.class));
		List<Person> persons = dataSet.collectAsList();
		persons.forEach(System.out::println);
				
		// Create DataFrame and Dataset from java Objects through SparkSession
		dataFrame = spark.createDataFrame(persons, Person.class);
		dataSet = spark.createDataset(persons, Encoders.bean(Person.class));
		
//		dataFrame.sqlContext().sql("select * from person");
//		sparkSession.sql("drop table if exists person");
//		sparkSession.sql("create table person (id int, name string, age int)");		
		
	}
	
}
