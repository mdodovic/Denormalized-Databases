package rs.ac.bg.etf.matija.DTtpcE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DenormalizedChemaLoader {

	public static void loadData(Connection databaseConnection) {
		// This will fill Denormalize schema with data from Normalized schema
		
		String getDataForDT = "SELECT CA_ID, HS_CA_ID, CA_BAL, HS_S_SYMB, LT_S_SYMB, HS_QTY, C_ID, CA_C_ID, LT_PRICE \r\n" + 
				"\r\n" + 
				"FROM dbo.CUSTOMER_ACCOUNT left outer join dbo.HOLDING_SUMMARY on HS_CA_ID = CA_ID, \r\n" + 
				"	dbo.LAST_TRADE, dbo.CUSTOMER\r\n" + 
				"\r\n" + 
				"WHERE LT_S_SYMB = HS_S_SYMB and C_ID = CA_C_ID ";
		
		//long timeBefore = System.nanoTime();

		System.out.println("Fetch data for denormalized table ... finished");
		
		try (Statement stmt = databaseConnection.createStatement();
			ResultSet rs = stmt.executeQuery(getDataForDT)){
				
			int total = 0;
			int successfull = 0;
			while(rs.next()) {

				/*if(rs.getLong("C_ID") != 4300000001l && rs.getLong("C_ID") != 4300000002l)
				{
					continue;
				}*/
				
				boolean successfullyInserted = insertIntoDatabase(databaseConnection, 
					rs.getLong("CA_ID"), rs.getDouble("CA_BAL"), 
					rs.getString("HS_S_SYMB"), rs.getInt("HS_QTY"), 
					rs.getLong("C_ID"), rs.getDouble("LT_PRICE"));
				
				total += 1;

				if(successfullyInserted) {
					successfull += 1;
				}
				
				/*
				if(rs.getLong("CA_ID") == 43000000001l)
				{				
					System.out.print(rs.getLong("CA_ID"));
					System.out.print(" ");
					System.out.print(rs.getDouble("CA_BAL"));
					System.out.print(" ");
					System.out.print(rs.getString("HS_S_SYMB"));
					System.out.print(" ");
					System.out.print(rs.getInt("HS_QTY"));
					System.out.print(" ");
					System.out.print(rs.getLong("C_ID"));
					System.out.print(" ");
					System.out.print(rs.getDouble("LT_PRICE"));
					System.out.println();
				}
				*/
				
				if(successfull % 10000 == 0) {
					System.out.println("Loaded rows: " + successfull);
				}

			}
			System.out.println("Total loaded rows: " + successfull + " (" + 
					String.format("%.2f", 100. * successfull / total) + "%)");
						
			
			
		} catch(SQLException e) { 
			e.printStackTrace(); 
		}
		
	}

	private static boolean insertIntoDatabase(Connection databaseConnection, 
			long ca_id, double ca_bal, String hs_s_symb,
			int hs_qty, long c_id, double lt_price) {

		String insertIntoDenormalizedTable = "INSERT INTO [tpcE].[dbo].[DTT2T3T8]\r\n" + 
				"           ([DT_CA_ID]\r\n" + 
				"           ,[DT_CA_BAL]\r\n" + 
				"           ,[DT_HS_S_SYMB]\r\n" + 
				"           ,[DT_HS_QTY]\r\n" + 
				"           ,[DT_CA_C_ID]\r\n" + 
				"           ,[DT_LT_PRICE])\r\n" + 
				"     VALUES\r\n" + 
				"           (?,?,?,?,?,?)";

		
		try (PreparedStatement stmt = databaseConnection.prepareStatement(insertIntoDenormalizedTable)){

			databaseConnection.setAutoCommit(false);
			
			stmt.setLong(1, ca_id);
			stmt.setDouble(2, ca_bal);			
			stmt.setString(3, hs_s_symb);
			stmt.setInt(4, hs_qty);			
			stmt.setLong(5, c_id);
			stmt.setDouble(6, lt_price);			

			stmt.executeUpdate();				

			databaseConnection.commit();
			return true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
			
				databaseConnection.rollback();
				return false;

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}finally {
			try {
				databaseConnection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		
		return true;
		
	}

}
