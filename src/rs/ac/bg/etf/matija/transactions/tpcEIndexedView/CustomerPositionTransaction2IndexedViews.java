package rs.ac.bg.etf.matija.transactions.tpcEIndexedView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import rs.ac.bg.etf.matija.transactions.CustomerPositionTransaction2;

public class CustomerPositionTransaction2IndexedViews extends CustomerPositionTransaction2 {

	
	
	public CustomerPositionTransaction2IndexedViews(Connection connection, long cust_id, String tax_id, int get_history,
			long acct_idx) {
		super(connection,cust_id,tax_id,get_history,acct_idx);		

	}

	@Override
	public void invokeCustomerPositionFrame1() {


		String getCustomerAccountInfo = "SELECT TOP 10 CA_ID, CA_BAL, RES_SUM\r\n" + 
				"FROM dbo.cA_Indexed WITH (NOEXPAND)\r\n" + 
				"WHERE CA_C_ID = ?\r\n" + 
				"order by 3 asc	";

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
	public void invokeCustomerPositionFrame2() {}

}
