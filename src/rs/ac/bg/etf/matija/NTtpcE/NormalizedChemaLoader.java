package rs.ac.bg.etf.matija.NTtpcE;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class NormalizedChemaLoader {

	private static final String pathToData = "D:\\diplomski_rad\\flat_out\\";
	

	public static void loadData(Connection connection) {
		
		List<String> listOfTableWithData = new LinkedList<String>();
		listOfTableWithData.add("CUSTOMER_ACCOUNT");
		listOfTableWithData.add("HOLDING_SUMMARY");
		listOfTableWithData.add("LAST_TRADE");
		listOfTableWithData.add("CUSTOMER");

		String bulkLoadPattern = "BULK INSERT tpcE.dbo.#1# \r\n" + 
				"FROM '" + pathToData + "#2#" + ".txt' \r\n" + 
				"WITH (BATCHSIZE = 20000, FIELDTERMINATOR = '|', ROWTERMINATOR = '\\n') \r\n";

		for(String tableName : MainNTtpcE.tableNames) {
			
			if(!listOfTableWithData.contains(tableName))
				continue;
			
			String[] name = tableName.split("_");
			
			String fileName = name[0].substring(0, 1) + name[0].substring(1).toLowerCase();
			if(name.length == 2)
				fileName += name[1].substring(0, 1) + name[1].substring(1).toLowerCase();
			
			String bulkLoadQuery = bulkLoadPattern.replace("#1#", tableName);
			bulkLoadQuery = bulkLoadQuery.replace("#2#", fileName);
			
			Statement stmt;
			try {
				stmt = connection.createStatement();
				stmt.executeUpdate(bulkLoadQuery);
				//System.out.println("Data added to table " + tableName);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("Data for " + tableName + " successfully loaded");

		}
		System.out.println("------------------------------------------------------------");
	}

}
