package rs.ac.bg.etf.matija.transactions.tpcEDenormalized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import rs.ac.bg.etf.matija.transactions.CustomerPositionTransaction2;

public class CustomerPositionTransaction2Denormalized extends CustomerPositionTransaction2{

	public CustomerPositionTransaction2Denormalized(Connection dbConn, long cust_id, String tax_id, int get_history, long acct_idx) {
		super(dbConn, cust_id, tax_id, get_history, acct_idx);		
	}


	@Override
	public void invokeCustomerPositionFrame1() {
		
		String getCustomerAccountInfo = "SELECT TOP 10 \r\n" + 
				"DT_CA_ID AS CA_ID, DT_CA_BAL AS CA_BAL, (sum(DT_HS_QTY * DT_LT_PRICE)) as RES_SUM\r\n" + 
				"\r\n" + 
				"FROM [tpcE].[dbo].[DTT2T3T8]\r\n" + 
				"  \r\n" + 
				"WHERE DT_CA_C_ID = ? and DT_HS_S_SYMB IS NOT NULL\r\n" + 
				"\r\n" + 
				"group by DT_CA_ID, DT_CA_BAL\r\n" + 
				"\r\n" + 
				"order by 3 asc";

		try (PreparedStatement stmt = databaseConnection.prepareStatement(getCustomerAccountInfo)){
			stmt.setLong(1, cust_id);
			try(ResultSet rs = stmt.executeQuery()){
				
				fillDataToCustomerAccountRows(rs);
				
				/*System.out.println(cust_id);
				for (Long key : this.customerAccountRowData.keySet()) {
				    System.out.println(key + ". " + customerAccountRowData.get(key));
				}*/
				
			}
		} catch(SQLException e) { 
			e.printStackTrace(); 
		}
		/*
		if (get_history == 1) {
			
			this.acct_id = customerAccountRowData.get(acct_idx + 1).CA_ID;
			
		}
		*/
		acc_len = customerAccountRowData.size();		

	}

	@Override
	public void invokeCustomerPositionFrame2() {
		// TODO Auto-generated method stub
		
	}
	
}
