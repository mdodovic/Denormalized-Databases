package rs.ac.bg.etf.matija.transactions.tpcEDenormalized;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import rs.ac.bg.etf.matija.transactions.MarketFeedTransaction3;

public class MarketFeedTransaction3Denormalized extends MarketFeedTransaction3 {

	public MarketFeedTransaction3Denormalized(Connection databaseConnection, double[] price_quote,
			String status_submitted, String[] symbol, long[] trade_qty, String type_limit_buy, String type_limit_sell,
			String type_stop_loss) {
		
		super(databaseConnection, price_quote, status_submitted, symbol, trade_qty, type_limit_buy, type_limit_sell,
				type_stop_loss);

	}

	@Override
	public void invokeMarketFeedFrame1() {

		for(int i = 0; i < price_quote.length/*Constraints.max_feed_len*/; i++) {
			// must be as a signle transaction with rollback mechanism

			String updateLastTrade = "UPDATE [tpcE].[dbo].[DTT2T3T8]\r\n" + 
					"   SET [DT_LT_PRICE] = ? \r\n" + 
					" WHERE [DT_HS_S_SYMB] = ? ";

			try (PreparedStatement stmt = databaseConnection.prepareStatement(updateLastTrade)){

				databaseConnection.setAutoCommit(false);
				
				stmt.setDouble(1, price_quote[i]);
				stmt.setString(2, symbol[i]);			

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

}
