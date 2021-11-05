package rs.ac.bg.etf.matija.transactions.tpcEDenormalized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import rs.ac.bg.etf.matija.transactions.TradeResultTransaction8;

public class TradeResultTransaction8Denormalized extends TradeResultTransaction8 {

	public TradeResultTransaction8Denormalized(Connection connection, long acct_id, String symbol, int hs_qty,
			int trade_qty, double se_amount) {
		
		super(connection, acct_id, symbol, hs_qty, trade_qty, se_amount);

	}

	@Override
	public void invokeTradeResultFrame2() {
		
		String updateHoldingSummary = "UPDATE [tpcE].[dbo].[DTT2T3T8]\r\n" + 
				"   SET [DT_HS_QTY] = ? + ?\r\n" + 
				"\r\n" + 
				"  where [DT_CA_ID] = ? and DT_HS_S_SYMB = ?";

		try (PreparedStatement stmt = databaseConnection.prepareStatement(updateHoldingSummary)){

			databaseConnection.setAutoCommit(false);
			
			stmt.setInt(1, hs_qty);
			stmt.setInt(2, trade_qty);			
			stmt.setLong(3, acct_id);
			stmt.setString(4, symbol);			

			stmt.executeUpdate();				

			databaseConnection.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				databaseConnection.rollback();
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

		
	}

	@Override
	public void invokeTradeResultFrame6() {


		String updateLastTrade = "UPDATE [tpcE].[dbo].[DTT2T3T8]\r\n" + 
				"SET [DT_CA_BAL] = [DT_CA_BAL] + ? \r\n" + 
				"WHERE [DT_CA_ID] = ?";

		
		try (PreparedStatement stmt = databaseConnection.prepareStatement(updateLastTrade)){

			databaseConnection.setAutoCommit(false);
			
			stmt.setDouble(1, se_amount);
			stmt.setLong(2, acct_id);			

			stmt.executeUpdate();				

			databaseConnection.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
			try {
				databaseConnection.rollback();
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

	}

}
