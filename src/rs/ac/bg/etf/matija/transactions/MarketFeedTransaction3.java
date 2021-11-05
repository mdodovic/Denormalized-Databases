package rs.ac.bg.etf.matija.transactions;

import java.sql.Connection;

public abstract class MarketFeedTransaction3 {

	
	protected double[] price_quote;
	//private String status_submitted;
	protected String[] symbol;
	protected long[] trade_qty;
	//private String type_limit_buy;
	//private String type_limit_sell;
	//private String type_stop_loss;
	
	protected Connection databaseConnection;
	public MarketFeedTransaction3(Connection databaseConnection, double[] price_quote, String status_submitted, String[] symbol, long[] trade_qty,
			String type_limit_buy, String type_limit_sell, String type_stop_loss) {

		this.price_quote = price_quote;
		this.symbol = symbol;
		this.trade_qty = trade_qty;
		this.databaseConnection = databaseConnection;
	}

	
	public abstract void invokeMarketFeedFrame1();
	
}
