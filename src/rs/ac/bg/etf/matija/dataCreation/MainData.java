package rs.ac.bg.etf.matija.dataCreation;

import rs.ac.bg.etf.matija.main.Main;

public class MainData {

	
	public static void main(String[] args) {
		Main database = new Main();		
		database.connectToMSSQL();
		// Data manipulation:	
		// T2T3T8 Data creation:
		
		DataT2T3T8 data = new DataT2T3T8(database.getConnection());
		data.dataGenerationT2ReadOnly(); //131182
		data.dataGenerationT3T8WriteOnly(); // 393546

		System.out.println("Transaction mix (" + DataT2T3T8.totalLineCount + " transactions) creattion ... finished");
	}
}
