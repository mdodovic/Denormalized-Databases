package rs.ac.bg.etf.matija.main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import rs.ac.bg.etf.matija.DTtpcE.DenormalizedChemaCreator;
import rs.ac.bg.etf.matija.DTtpcE.DenormalizedChemaLoader;
import rs.ac.bg.etf.matija.DTtpcE.MainDTtpcE;
import rs.ac.bg.etf.matija.MWtpcE.IndexedViewsCreator;
import rs.ac.bg.etf.matija.MWtpcE.MainMWtpcE;
import rs.ac.bg.etf.matija.NTtpcE.MainNTtpcE;
import rs.ac.bg.etf.matija.NTtpcE.NormalizedChemaCreator;
import rs.ac.bg.etf.matija.NTtpcE.NormalizedChemaLoader;

public class Main {

	public static final String USER = "sa";
	public static final String PASSWORD = "matija";

	private Connection connection;

	public static final String pathToResultFolderNormalized = "./src/time_results/normalized/";
	public static final String pathToResultFolderIndexes = "./src/time_results/indexes/";
	public static final String pathToResultFolderDenormalized = "./src/time_results/denormalized/";
	
	public static final String inputDataFile = "inputData/T2T3T8_T2F1_read_130k.sql";
	public static final String outputResultFile = "T2F1_read_130k";
	//public static final String inputDataFile = "inputData/T2T3T8_T3F1_write_130k.sql";
	//public static final String outputResultFile = "T3F1_write_130k"; 
	//public static final String inputDataFile = "inputData/T2T3T8_T8F2_write_130k.sql";
	//public static final String outputResultFile = "T8F2_write_130k";
	//public static final String inputDataFile = "inputData/T2T3T8_T8F6_write_130k.sql";
	//public static final String outputResultFile = "T8F6_write_130k";
	//public static final String inputDataFile = "inputData/T2T3T8_T3T8_all_write_130k.sql";
	//public static final String outputResultFile = "T3T8_all_write_130k";
	
	public Connection getConnection() {
		return connection;
	}
	
	public void connectToMSSQL() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		
			String x = "jdbc:sqlserver://localhost:1433;databaseName=tpcE;";
	
			connection = DriverManager.getConnection(x, USER, PASSWORD);

			System.out.println("Connected to MS SQL Server");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void tpcENormalized(String fileName) {
		long start = System.nanoTime();

		
		Main database = new Main();		
		database.connectToMSSQL();
		
		try (FileWriter fw1 = new FileWriter(pathToResultFolderNormalized + fileName +"_timestamp.txt");
			PrintWriter timestamp = new PrintWriter(fw1);
				FileWriter fw2 = new FileWriter(pathToResultFolderNormalized + fileName + "_difference.txt");
				PrintWriter difference = new PrintWriter(fw2)){
		
			// Drop all indexed views if exists
			IndexedViewsCreator.dropIndexes(database.getConnection());
			System.out.println("All indexes dropped ... finished");
	
			// Tpce Normalized schema:
			MainNTtpcE tpcEOriginalSchema = new MainNTtpcE(database.getConnection());
			
			//Drop whole schematic, this will be automatically done when the new schematic is created. 
			/*
			try {
				NormalizedChemaCreator.dropNormalizedDatabaseChema(database.getConnection());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			*/
			
			// Create normalized schema (first drop schema, then create full schema)
			NormalizedChemaCreator.createNormalizedDatabaseChema(database.getConnection());
			//$System.out.println(System.nanoTime() - start);
			System.out.println("Database schema creation ... finished");
			
			NormalizedChemaLoader.loadData(database.getConnection());
			//$System.out.println(System.nanoTime() - start);
			
			NormalizedChemaCreator.createIndexes(database.getConnection());
			System.out.println("Loading data ... finished");
			long coldStart = System.nanoTime() - start;
			System.out.println("Cold start ... finished after " + (coldStart) + " nanoseconds");
			
			tpcEOriginalSchema.startTransactionMixture(Main.inputDataFile, timestamp, difference);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void tpcEIndexed(String fileName) {
		long start = System.nanoTime();

		Main database = new Main();		
		database.connectToMSSQL();

		
		try (FileWriter fw1 = new FileWriter(pathToResultFolderIndexes + fileName +"_timestamp.txt");
				PrintWriter timestamp = new PrintWriter(fw1);
					FileWriter fw2 = new FileWriter(pathToResultFolderIndexes + fileName + "_difference.txt");
					PrintWriter difference = new PrintWriter(fw2)){
			
			MainMWtpcE tpcEIndexedSchema = new MainMWtpcE(database.getConnection());

			IndexedViewsCreator.dropIndexes(database.getConnection());
			System.out.println("All indexes dropped ... finished");
			
			NormalizedChemaCreator.createNormalizedDatabaseChema(database.getConnection());
			System.out.println("Database schema creation ... finished");
			
			NormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data ... finished");

			IndexedViewsCreator.createIndexView(database.getConnection()); //........database...
			System.out.println("Create materialized view ... finished");

			
			long coldStart = System.nanoTime() - start;
			System.out.println("Cold start ... finished after " + (coldStart) + " nanoseconds");
			
			tpcEIndexedSchema.startTransactionMixture(Main.inputDataFile, timestamp, difference);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}
	
	
	public static void tpcEDenormalized(String fileName) {
		long start = System.nanoTime();
		
		Main database = new Main();		
		database.connectToMSSQL();

		try (FileWriter fw1 = new FileWriter(pathToResultFolderDenormalized + fileName +"_timestamp.txt");
				PrintWriter timestamp = new PrintWriter(fw1);
					FileWriter fw2 = new FileWriter(pathToResultFolderDenormalized + fileName + "_difference.txt");
					PrintWriter difference = new PrintWriter(fw2)){

			// Tpce Normalized schema:
			MainDTtpcE tpcEDTSchema = new MainDTtpcE(database.getConnection());
			
			IndexedViewsCreator.dropIndexes(database.getConnection());
			System.out.println("All indexes dropped ... finished");

			NormalizedChemaCreator.createNormalizedDatabaseChema(database.getConnection());
			System.out.println("Database schema creation ... finished");
			
			NormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data ... finished");

			DenormalizedChemaCreator.createDenormalizedDatabaseChema(database.getConnection());
			System.out.println("Denormalized schemas creation ... finished");

			DenormalizedChemaLoader.loadData(database.getConnection());
			System.out.println("Loading data to denormalized schema ... finished");
			
			DenormalizedChemaCreator.createIndexes(database.getConnection());
			System.out.println("Rising indexes on columns in denormalized schema ... finished");
			
			long coldStart = System.nanoTime() - start;
			System.out.println("Cold start ... finished after " + (coldStart) + " nanoseconds");

			tpcEDTSchema.startTransactionMixture(Main.inputDataFile, timestamp, difference);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}	
	
	
	public static void main(String[] args) {
		
		
		tpcENormalized(outputResultFile);
		
		//tpcEIndexed(outputResultFile);
		
		//tpcEDenormalized(outputResultFile);
		
	}

}
