package rs.ac.bg.etf.matija.NTtpcE;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.stream.Stream;

import rs.ac.bg.etf.matija.main.Constraints;
import rs.ac.bg.etf.matija.main.Main;
import rs.ac.bg.etf.matija.transactions.CustomerPositionTransaction2;
import rs.ac.bg.etf.matija.transactions.MarketFeedTransaction3;
import rs.ac.bg.etf.matija.transactions.TradeResultTransaction8;
import rs.ac.bg.etf.matija.transactions.tpcENormalized.CustomerPositionTransaction2Normalized;
import rs.ac.bg.etf.matija.transactions.tpcENormalized.MarketFeedTransaction3Normalized;
import rs.ac.bg.etf.matija.transactions.tpcENormalized.TradeResultTransaction8Normalized;


public class MainNTtpcE {

	public static final String[] tableNames = new String[] {
			"HOLDING",
			"TRADE_HISTORY",
			"SETTLEMENT",
			"CASH_TRANSACTION",
			"HOLDING_HISTORY",
			"TRADE_REQUEST",
			"TRADE",
			"ACCOUNT_PERMISSION",
			"HOLDING_SUMMARY",
			"DAILY_MARKET",
			"WATCH_ITEM",
			"CUSTOMER_TAXRATE",
			"FINANCIAL",
			"NEWS_XREF",
			"LAST_TRADE",
			"CUSTOMER_ACCOUNT",
			"COMPANY_COMPETITOR",
			"SECURITY",
			"COMPANY",
			"COMMISSION_RATE",
			"EXCHANGE",
			"WATCH_LIST",
			"CUSTOMER",
			"ADDRESS",
			"ZIP_CODE",
			"TAXRATE",
			"BROKER",
			"STATUS_TYPE",
			"INDUSTRY",
			"SECTOR",
			"NEWS_ITEM",
			"CHARGE",
			"TRADE_TYPE"		
		};

	
	private int status;

	private Connection connection;
	
	public MainNTtpcE(Connection connection) {
		this.connection = connection;
	}

	public void startCustomerPositionTransaction(long cust_id, String tax_id, int get_history, int acct_idx) {

		CustomerPositionTransaction2 cpT2 = new CustomerPositionTransaction2Normalized(this.connection,cust_id, tax_id,get_history,acct_idx);
		// Transaction:
		// Frame 1:
		cpT2.invokeCustomerPositionFrame1();
		
		if (cpT2.acc_len < 1 || cpT2.acc_len > Constraints.max_acct_len_rows) {
			this.status = -211;
			if(status < 0) {
				System.err.println("Unexpected error!");
			}
		}
		
		// Frame 2:
		if (get_history == 1) {

			cpT2.invokeCustomerPositionFrame2();
			
			if (cpT2.hist_len < 10 || cpT2.acc_len > Constraints.max_hist_len_rows) {
				this.status = -211;
				if(status < 0) {
					System.err.println("Unexpected error!");
				}
			}
			
		}
		
	}
	
	public void startMarketFeedTransaction(double[] price_quote, String status_submitted, String[] symbol, long[] trade_qty, String type_limit_buy, String type_limit_sell, String type_stop_loss) {

		MarketFeedTransaction3 T3 = new MarketFeedTransaction3Normalized(this.connection,
				price_quote,
				status_submitted,
				symbol,
				trade_qty,
				type_limit_buy,
				type_limit_sell,
				type_stop_loss
				);
		// Transaction:
		// Frame 1:
		T3.invokeMarketFeedFrame1();

	}
	
	public void startTradeResult(long acct_id, String symbol, int hs_qty, int trade_qty, double se_amount, int trNum) {
		
		TradeResultTransaction8 T8 = new TradeResultTransaction8Normalized(this.connection,
				acct_id, symbol, hs_qty, trade_qty, se_amount);
		
		// Transaction:
		// Frame 1:
		// Frame 2:
		if(trNum == 2)
			T8.invokeTradeResultFrame2();
		// Frame 3:
		// Frame 4:
		// Frame 5:
		// Frame 6:
		if(trNum == 6)
			T8.invokeTradeResultFrame6();

	}
	
	public void startTransactionMixture(String pathToData, PrintWriter timestamp, PrintWriter difference) {

		long totalLineCounter = 0;
		try (Stream<String> stream = Files.lines(Paths.get(Main.inputDataFile), StandardCharsets.UTF_8)) {
			totalLineCounter = stream.count();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long readTransactionCounter = 0;
		long writeTransactionCounter = 0;

		long lineCounter = 0;
		long currentTime = System.nanoTime();
		
		timestamp.write("" + System.nanoTime() + "\n");
		
		try (FileReader fr = new FileReader(pathToData);
			BufferedReader br = new BufferedReader(fr)){
			String s;
			while((s = br.readLine()) != null){
				String[] parsedTransaction = s.split(" ");
				
				if ("CustomerPositionFrame1".equals(parsedTransaction[1])) {

					String[] data = parsedTransaction[2].split(",");
					long cust_id = Long.parseLong(data[0]);
					String tax_id = data[1];
					int get_history = 0;
					int acct_idx = -1;
//					System.out.println(cust_id + ", " + tax_id + ", " +  get_history + ", " + acct_idx);

					long startTransaction = System.nanoTime();

					startCustomerPositionTransaction(cust_id, tax_id, get_history, acct_idx);

					difference.write("" + (System.nanoTime() - startTransaction) + "\n");
					//System.out.println((System.nanoTime() - startTransaction));
					readTransactionCounter++;

				} 
				if ("MarketFeedFrame1".equals(parsedTransaction[1])) {

					String[] data = parsedTransaction[2].split(",");

					double[] price_quote = new double[] {Double.parseDouble(data[0])};
					String status_submitted = data[1];
					String[] symbol = new String[]{data[2]};
					long[] trade_qty = new long[] {Long.parseLong(data[3])};
					String type_limit_buy = "";
					String type_limit_sell = "";
					String type_stop_loss = "";
					
					long startTransaction = System.nanoTime();
					
					startMarketFeedTransaction(price_quote, status_submitted, 
							symbol, trade_qty, type_limit_buy, type_limit_sell,type_stop_loss);					

					difference.write("" + (System.nanoTime() - startTransaction) + "\n");

					writeTransactionCounter++;
				}
				if ("TradeResultFrame2".equals(parsedTransaction[1])) {

					String[] data = parsedTransaction[2].split(",");
					
					long acct_id = Long.parseLong(data[0]);
					int hs_qty = Integer.parseInt(data[1]);				
					String symbol = data[2];
					int trade_qty = Integer.parseInt(data[3]);

					long startTransaction = System.nanoTime();
					
					startTradeResult(acct_id, symbol, hs_qty, trade_qty, -1., 2);

					difference.write("" + (System.nanoTime() - startTransaction) + "\n");
					
					writeTransactionCounter++;
				} 					
				if ("TradeResultFrame6".equals(parsedTransaction[1])) {

					String[] data = parsedTransaction[2].split(",");
					long acct_id = Long.parseLong(data[0]);
					double se_amount = Double.parseDouble(data[1]);

					long startTransaction = System.nanoTime();
					
					startTradeResult(acct_id, "", -1, -1, se_amount, 6);
	
					difference.write("" + (System.nanoTime() - startTransaction) + "\n");

					writeTransactionCounter++;
				} 					
				lineCounter ++;
				if(lineCounter % 1000 == 0) {
					System.out.println("Finished " + 
							String.format("%.2f", 100. * lineCounter / totalLineCounter) 
							+ "% transactions ( w: " + writeTransactionCounter + 
							"; r: " + readTransactionCounter + ")");
				}
				timestamp.write("" + System.nanoTime() + "\n");

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		currentTime = System.nanoTime() - currentTime;
		System.out.println("Finish after: " + currentTime + " nanoseconds");

		System.out.println("Read transactions: " + readTransactionCounter);
		System.out.println("Write transactions: " + writeTransactionCounter);

	}

}
