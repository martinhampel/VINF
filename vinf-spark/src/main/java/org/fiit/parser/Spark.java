package org.fiit.parser;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

//The Spark class has a single method named parse_people that takes a file parameter, which is the path to the file to be parsed. This method does the following:
//
//        It creates a new SparkSession object, which is the entry point for working with Spark.
//        It reads the file and divides it into columns using the delimiter option and the split and regexp_extract functions. The resulting DataFrame contains columns for the objectId, typeName, and typeValue of each record in the file.
//        It filters the DataFrame to include only records that represent people, and selects the objectId and type columns from these records.
//        It filters the DataFrame to include only records that represent the names of people, and selects the id_name and name columns from these records.
//        It filters the DataFrame to include only records that represent the dates of birth of people, and selects the id_birth and birth_date columns from these records.
//        It filters the DataFrame to include only records that represent the dates of death of people, and selects the id_death and death_date columns from these records.
//        It joins the four filtered DataFrames together using their common id columns, and selects the id, type, name, birth_date, and death_date columns from the resulting DataFrame.
//        It filters the resulting DataFrame to include only records where the birth_date and name columns are not empty, and drops any duplicate records based on the id column.
//        It writes the resulting DataFrame to a CSV file.
//        The parse_people method uses the Spark SQL API to read and transform the data in the file, making it easier to work with and analyze. The resulting CSV file contains the cleaned and transformed data, which can be used for further analysis or indexing.

public class Spark {
        public void parse_people(String file) {
            SparkSession spark = SparkSession.builder().master("local[*]").appName("PeopleFinder").getOrCreate();

            Dataset<Row> divided_file = spark.read().option("delimiter", " ").text(file);
            divided_file = divided_file.select(
                    split(col("value"), "\t").getItem(0).as("objectId"),
                    split(col("value"), "\t").getItem(1).as("typeName"),
                    split(col("value"), "\t").getItem(2).as("typeValue")
            ).select(
                    regexp_extract(col("objectId"), "<\\w+[:/]+[a-zA-Z.]+/\\w+/(.\\.\\w+)>", 1).as("objectId"),
                    col("typeName"),
                    col("typeValue")
            );

            Dataset<Row> divided_people = divided_file
                    .filter(col("typeValue").rlike(".*<http://rdf\\.freebase\\.com/ns/people\\.person>.*|.*<http://rdf\\.freebase\\.com/ns/people\\.deceased_person>.*"))
                    .select(col("objectId").as("id"),
                            regexp_extract(col("typeValue"), "<\\w+[:/]+[a-zA-Z.]+/\\w+/([a-zA-Z._]+)>", 1).as("type")
                    );

            Dataset<Row> columns_name = divided_file
                    .filter(col("typeName").rlike("<http://rdf.freebase.com/ns/type.object.name>"))
                    .select(col("objectId").as("id_name"),
                            regexp_extract(col("typeValue"), "\"((\\w+[ ]*)*)\"@(\\w+)", 1).as("name")
                    );

            Dataset<Row> columns_birth = divided_file
                    .filter(col("typeName").rlike("<http://rdf.freebase.com/ns/people.person.date_of_birth>"))
                    .select(col("objectId").as("id_birth"),
                            regexp_extract(col("typeValue"), "\"((\\d+[:\\-/]*)+)\".*", 1).as("birth_date")
                    );

            Dataset<Row> columns_death = divided_file
                    .filter(col("typeName").rlike("<http://rdf.freebase.com/ns/people.deceased_person.date_of_death>"))
                    .select(col("objectId").as("id_death"),
                            regexp_extract(col("typeValue"), "\"((\\d+[:\\-/]*)+)\".*", 1).as("death_date")
                    );

            Dataset<Row> joined_file = divided_people
                    .join(columns_name, divided_people.col("id").equalTo(columns_name.col("id_name")))
                    .join(columns_birth, divided_people.col("id").equalTo(columns_birth.col("id_birth")))
                    .join(columns_death, divided_people.col("id").equalTo(columns_death.col("id_death")), "left_outer")
                    .select(col("id"),
                            col("type"),
                            col("name"),
                            col("birth_date"),
                            col("death_date"))
                    .filter("birth_date != '' AND name != '' ")
                    .dropDuplicates("id");

            joined_file.coalesce(1).write().format("csv").option("header", true).save("parsed-data/parsed-people");
        }
}