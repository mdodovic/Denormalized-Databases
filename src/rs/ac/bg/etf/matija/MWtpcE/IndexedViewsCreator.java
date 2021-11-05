package rs.ac.bg.etf.matija.MWtpcE;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class IndexedViewsCreator {

	
	
	public static void createIndexView(Connection connection) {
		
		
		try {

			Statement stmt;
			
			String setOptionsToSupportIndexes = "SET NUMERIC_ROUNDABORT OFF;\r\n" + 
					"SET ANSI_PADDING, ANSI_WARNINGS, CONCAT_NULL_YIELDS_NULL, ARITHABORT, QUOTED_IDENTIFIER, ANSI_NULLS ON;";
			
			stmt = connection.createStatement();
			stmt.executeUpdate(setOptionsToSupportIndexes);


			
			String createView = "CREATE VIEW [dbo].[cA_indexed]\r\n" + 
					"WITH SCHEMABINDING\r\n" + 
					"AS  \r\n" + 
					"	SELECT CA_C_ID, CA_ID, CA_BAL, ((sum(HS_QTY * LT_PRICE))) as RES_SUM, COUNT_BIG(*) AS CB\r\n" + 
					"    FROM dbo.CUSTOMER_ACCOUNT inner join dbo.HOLDING_SUMMARY on HS_CA_ID = CA_ID, dbo.LAST_TRADE\r\n" + 
					"	WHERE LT_S_SYMB = HS_S_SYMB\r\n" + 
					"	group by CA_C_ID, CA_ID, CA_BAL";
			
			stmt = connection.createStatement();
			stmt.executeUpdate(createView);

			String createIndexOnView = "CREATE UNIQUE CLUSTERED INDEX IDX_V1\r\n" + 
					"   ON dbo.cA_indexed (CA_C_ID, CA_ID);";
			
			stmt = connection.createStatement();
			stmt.executeUpdate(createIndexOnView);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void dropIndexes(Connection connection) {
		try {
			
			
			Statement stmt;
			
			String dropIndexIfExist = "IF OBJECT_ID ('dbo.cA_indexed', 'view') IS NOT NULL\r\n" + 
				"   DROP VIEW dbo.cA_indexed ;";
		
			stmt = connection.createStatement();
			stmt.executeUpdate(dropIndexIfExist);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


}
