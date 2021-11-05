package rs.ac.bg.etf.matija.transactions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import rs.ac.bg.etf.matija.transactions.tpcENormalized.CustomerPositionTransaction2Normalized;


public abstract class CustomerPositionTransaction2 {
	
	
	public int acc_len;
	public int hist_len;

	protected long cust_id;
	protected String tax_id;
	protected int get_history;
	protected long acct_idx;
	
	protected Connection databaseConnection;
	protected long acct_id;
	
	// CARows 
	protected long rowidCustomerAccountRows = 1; // identity(1,1): start value is 1 and it is incremented by 1 every time
	protected Map<Long, CustomerAccountRow> customerAccountRowData = 
			new HashMap<Long, CustomerPositionTransaction2Normalized.CustomerAccountRow>();
	
	// CustomerRows
	protected long rowidCustomerRows = 1; // identity(1,1)
	protected Map<Long, CustomerRow> customerRowData = 
			new HashMap<Long, CustomerPositionTransaction2Normalized.CustomerRow>();
	
	// TradeRows
	protected long rowidTradeRows = 1; // identity(1,1)
	protected Map<Long, TradeRow> tradeRowData = 
			new HashMap<Long, CustomerPositionTransaction2Normalized.TradeRow>();
	
	

	
	public CustomerPositionTransaction2(Connection dbConn, long cust_id, String tax_id, int get_history, long acct_idx) {
		super();
		this.cust_id = cust_id;
		this.tax_id = tax_id;
		this.get_history = get_history;
		this.acct_idx = acct_idx;
		this.databaseConnection = dbConn;
	}
	
	public abstract void invokeCustomerPositionFrame1();
	public abstract void invokeCustomerPositionFrame2();
	
	
	
	protected void fillDataToTradeRows(ResultSet rs) throws SQLException {
		
		while (rs.next()) {
			TradeRow tr = new TradeRow();
			
			tr.ST_NAME = rs.getString("ST_NAME");
			tr.T_ID = rs.getLong("ID");
			tr.T_QTY = rs.getInt("T_QTY");
			tr.T_S_SYMB = rs.getString("T_S_SYMB");
			tr.TH_DTS = rs.getTimestamp("TH_DTS");
			
			tradeRowData.put(rowidTradeRows, tr);
			rowidTradeRows += 1;
		}

		
	}
	
	protected void fillDataToCustomerAccountRows(ResultSet rs) throws SQLException {
		while (rs.next()) {
			CustomerAccountRow car = new CustomerAccountRow();
			car.CA_BAL = rs.getDouble("CA_BAL");
			car.CA_ID = rs.getLong("CA_ID");
			car.result = rs.getLong("RES_SUM");

			customerAccountRowData.put(rowidCustomerAccountRows, car);
			rowidCustomerAccountRows += 1;
		}

		
	}
	
	protected void fillDataToCustomerRows(ResultSet rs) throws SQLException {
		//ResultSetMetaData rsmd = rs.getMetaData(); // contains number of columns
		while (rs.next()) {
			CustomerRow cr = new CustomerRow();
			cr.C_AD_ID = rs.getLong("C_AD_ID");
			cr.C_AREA_1 = rs.getString("C_AREA_1");
			cr.C_AREA_2 = rs.getString("C_AREA_2");
			cr.C_AREA_3 = rs.getString("C_AREA_3");
			cr.C_CTRY_1 = rs.getString("C_CTRY_1");
			cr.C_CTRY_2 = rs.getString("C_CTRY_2");
			cr.C_CTRY_3 = rs.getString("C_CTRY_3");
			cr.C_CTRY_1 = rs.getString("C_CTRY_1");
			cr.C_CTRY_2 = rs.getString("C_CTRY_2");
			cr.C_CTRY_3 = rs.getString("C_CTRY_3");
			cr.C_DOB = rs.getDate("C_DOB");
			cr.C_EMAIL_1 = rs.getString("C_EMAIL_1");
			cr.C_EMAIL_2 = rs.getString("C_EMAIL_2");
			cr.C_EXT_1 = rs.getString("C_EXT_1");
			cr.C_EXT_2 = rs.getString("C_EXT_2");
			cr.C_EXT_3 = rs.getString("C_EXT_3");
			cr.C_F_NAME = rs.getString("C_F_NAME");
			cr.C_GNDR = rs.getString("C_GNDR");
			cr.C_L_NAME = rs.getString("C_L_NAME");
			cr.C_LOCAL_1 = rs.getString("C_LOCAL_1");
			cr.C_LOCAL_2 = rs.getString("C_LOCAL_2");
			cr.C_LOCAL_3 = rs.getString("C_LOCAL_3");
			cr.C_M_NAME = rs.getString("C_M_NAME");
			cr.C_ST_ID = rs.getString("C_ST_ID");
			cr.C_TIER = rs.getInt("C_TIER");
			
			customerRowData.put(rowidCustomerRows, cr);
			rowidCustomerRows += 1;
		}
		
	}
	
	public class CustomerRow {

		private String C_ST_ID;
		private String C_L_NAME;
		private String C_F_NAME;
		private String C_M_NAME;
		private String C_GNDR;
		private int C_TIER;
		private Date C_DOB;
		private long C_AD_ID;
	
		private String C_CTRY_1;
		private String C_AREA_1;
		private String C_LOCAL_1;
		private String C_EXT_1;
	
		private String C_CTRY_2;
		private String C_AREA_2;
		private String C_LOCAL_2;
		private String C_EXT_2;
	
		private String C_CTRY_3;
		private String C_AREA_3;
		private String C_LOCAL_3;
		private String C_EXT_3;
	
		private String C_EMAIL_1;
		private String C_EMAIL_2; 
		
		@Override
		public String toString() {
			return C_ST_ID + " " + 
			C_L_NAME + " " + 
			C_F_NAME + " " + 
			C_M_NAME + " " + 
			C_GNDR + " " + 
			C_TIER + " " + 
			C_DOB + " " + 
			C_AD_ID + " " + 
		
			C_CTRY_1 + " " + 
			C_AREA_1 + " " + 
			C_LOCAL_1 + " " + 
			C_EXT_1 + " " + 
		
			C_CTRY_2 + " " + 
			C_AREA_2 + " " + 
			C_LOCAL_2 + " " + 
			C_EXT_2 + " " + 
		
			C_CTRY_3 + " " + 
			C_AREA_3 + " " + 
			C_LOCAL_3 + " " + 
			C_EXT_3 + " " + 
		
			C_EMAIL_1 + " " + 
			C_EMAIL_2;

		}
	}

	public class CustomerAccountRow {
		private long CA_ID;		
		private double CA_BAL;
		private double result; // decimal(20,2)
		
		@Override
		public String toString() {
			return 	CA_ID + " " + 
					CA_BAL + " " + 
					result;
		}
	}
	
	public class TradeRow {

		private long T_ID;
		private String T_S_SYMB;
		private int T_QTY;
		private String ST_NAME;
		private Timestamp TH_DTS;
	
		@Override
		public String toString() {
			return 	T_ID + " " + 
					T_S_SYMB + " " + 
					T_QTY + " " + 
					ST_NAME + " " +
					TH_DTS;
		}
		
	}
	
}
