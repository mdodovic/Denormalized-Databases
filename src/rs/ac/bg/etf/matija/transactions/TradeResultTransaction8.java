package rs.ac.bg.etf.matija.transactions;

import java.sql.Connection;

public abstract class TradeResultTransaction8 {
	
	
	protected long acct_id;

	protected String symbol;
	protected int hs_qty;
	protected int trade_qty;

	protected double se_amount;
	protected Connection databaseConnection;
	
	public TradeResultTransaction8(Connection connection, 
			long acct_id, String symbol, int hs_qty, int trade_qty, double se_amount) {

		this.acct_id = acct_id;
		this.symbol = symbol;
		this.hs_qty = hs_qty;
		this.trade_qty = trade_qty;
		this.se_amount = se_amount;
		this.databaseConnection = connection;
	}

	public abstract void invokeTradeResultFrame2();
	public abstract void invokeTradeResultFrame6();
}
