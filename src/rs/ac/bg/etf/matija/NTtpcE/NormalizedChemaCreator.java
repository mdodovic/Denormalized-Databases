package rs.ac.bg.etf.matija.NTtpcE;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;


public class NormalizedChemaCreator {
	
	public static void createNormalizedDatabaseChema(Connection conn) {
		
		try {
			dropNormalizedDatabaseChema(conn);
			String createChemaQuery = "";
			Collections.reverse(Arrays.asList(MainNTtpcE.tableNames));
			for(String tableName: MainNTtpcE.tableNames) {
				
				createChemaQuery += createTableQuerry(tableName) + "\r\n";
				
			}
						
			Statement stmt;
			stmt = conn.createStatement();
			stmt.executeUpdate(createChemaQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(String tableName: MainNTtpcE.tableNames) {

			System.out.println("Table " + tableName + " successfully created");
			
		} 
		System.out.println("------------------------------------------------------------");

	}
	
	public static void dropNormalizedDatabaseChema(Connection conn) throws SQLException  {
		
		String dropQueryPattern = "IF EXISTS ( SELECT * FROM tpcE.INFORMATION_SCHEMA.TABLES where TABLE_NAME = '###' AND TABLE_SCHEMA = 'dbo') DROP TABLE dbo.###;";

		String dropQuery;
		Statement stmt;
		
		for(String tableName: MainNTtpcE.tableNames) {
			dropQuery = dropQueryPattern.replace("###",  tableName);
		
			stmt = conn.createStatement();
			stmt.executeUpdate(dropQuery);

			System.out.println("Table: " + tableName + " successfully deleted");

		}
		System.out.println("------------------------------------------------------------");
	}
	
	
	public static void createIndexes(Connection connection) {
		String indexName;
		String tableName;
		String createIndex;
		String columnName;
		String createIndexPattern;
		Statement stmt;
		
		/*
		String indexName = "Customer_Index_C_TAX_ID";
		String tableName = "CUSTOMER";
		String createIndex = "drop index if exists [Customer_Index_C_TAX_ID] on [dbo].[CUSTOMER];\r\n" + 
				"		create nonclustered index [Customer_Index_C_TAX_ID] on [dbo].[CUSTOMER] ([C_TAX_ID]);";

		
		try {
			stmt = connection.createStatement();

			stmt.execute(createIndex);

			System.out.println("Index: " + indexName + " successfully created");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		columnName = "CA_C_ID";
		indexName = "Customer_Account_Index_CA_C_ID";
		tableName = "CUSTOMER_ACCOUNT";
		createIndexPattern = "drop index if exists [#2#] on [dbo].[#1#];\r\n" + 
				"		create nonclustered index [#2#] on [dbo].[#1#] ([#3#]);";
		
		createIndex = createIndexPattern.replace("#1#",  tableName);
		createIndex = createIndex.replace("#2#",  indexName);
		createIndex = createIndex.replace("#3#",  columnName);
		
		try {
			stmt = connection.createStatement();

			stmt.execute(createIndex);

			System.out.println("Index: " + indexName + " successfully created");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	
	
	private static String createTableQuerry(String tableName) {
		
		switch(tableName) {
			case "HOLDING": return "CREATE TABLE [HOLDING] (\r\n" + 
					"	[H_T_ID] bigint Not Null,\r\n" + 
					"	[H_CA_ID] bigint Not Null,\r\n" + 
					"	[H_S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[H_DTS] DATETIME Not Null,\r\n" + 
					"	[H_PRICE] decimal(10,2) Not Null,\r\n" + 
					"	[H_QTY] int Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [HOLDING] ADD CONSTRAINT HOLDING_PK PRIMARY KEY (H_T_ID); \r\n" + 
					"ALTER TABLE [HOLDING] ADD CONSTRAINT FK_HOLDING_H_T_ID_TRADE_T_ID FOREIGN KEY (H_T_ID) REFERENCES TRADE(T_ID);\r\n" + 
					"ALTER TABLE [HOLDING] ADD CONSTRAINT FK_HOLDING_H_CA_ID_H_S_SYMB_HOLDING_SUMMARY_HS_CA_ID_HS_S_SYMB FOREIGN KEY (H_CA_ID,H_S_SYMB) REFERENCES HOLDING_SUMMARY(HS_CA_ID,HS_S_SYMB);\r\n" ;			
			
			case "TRADE_HISTORY": return "CREATE TABLE [TRADE_HISTORY] (\r\n" + 
					"	[TH_T_ID] bigint Not Null,\r\n" + 
					"	[TH_DTS] DATETIME Not Null,\r\n" + 
					"	[TH_ST_ID] CHAR(4) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [TRADE_HISTORY] ADD CONSTRAINT TRADE_HISTORY_PK PRIMARY KEY (TH_T_ID,TH_ST_ID); \r\n" + 
					"ALTER TABLE [TRADE_HISTORY] ADD CONSTRAINT FK_TRADE_HISTORY_TH_T_ID_TRADE_T_ID FOREIGN KEY (TH_T_ID) REFERENCES TRADE(T_ID);\r\n" + 
					"ALTER TABLE [TRADE_HISTORY] ADD CONSTRAINT FK_TRADE_HISTORY_TH_ST_ID_STATUS_TYPE_ST_ID FOREIGN KEY (TH_ST_ID) REFERENCES STATUS_TYPE(ST_ID);\r\n";
			
			
			case "SETTLEMENT": return "CREATE TABLE [SETTLEMENT] (\r\n" + 
					"	[SE_T_ID] bigint Not Null,\r\n" + 
					"	[SE_CASH_TYPE] CHAR(40) Not Null,\r\n" + 
					"	[SE_CASH_DUE_DATE] DATE Not Null,\r\n" + 
					"	[SE_AMT] decimal(15,2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [SETTLEMENT] ADD CONSTRAINT SETTLEMENT_PK PRIMARY KEY (SE_T_ID); \r\n" + 
					"ALTER TABLE [SETTLEMENT] ADD CONSTRAINT FK_SETTLEMENT_SE_T_ID_TRADE_T_ID FOREIGN KEY (SE_T_ID) REFERENCES TRADE(T_ID);\r\n";

			
			case "CASH_TRANSACTION": return "CREATE TABLE [CASH_TRANSACTION] (\r\n" + 
					"	[CT_T_ID] bigint Not Null,\r\n" + 
					"	[CT_DTS] DATETIME Not Null,\r\n" + 
					"	[CT_AMT] decimal(15,2) Not Null,\r\n" + 
					"	[CT_NAME] CHAR(100) \r\n" + 
					");\r\n" + 
					"ALTER TABLE [CASH_TRANSACTION] ADD CONSTRAINT CASH_TRANSACTION_PK PRIMARY KEY (CT_T_ID); \r\n" + 
					"ALTER TABLE [CASH_TRANSACTION] ADD CONSTRAINT FK_CASH_TRANSACTION_CT_T_ID_TRADE_T_ID FOREIGN KEY (CT_T_ID) REFERENCES TRADE(T_ID);\r\n";

			
			case "HOLDING_HISTORY": return "CREATE TABLE [HOLDING_HISTORY] (\r\n" + 
					"	[HH_H_T_ID] bigint Not Null,\r\n" + 
					"	[HH_T_ID] bigint Not Null,\r\n" + 
					"	[HH_BEFORE_QTY] int Not Null,\r\n" + 
					"	[HH_AFTER_QTY] int Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [HOLDING_HISTORY] ADD CONSTRAINT HOLDING_HISTORY_PK PRIMARY KEY (HH_H_T_ID,HH_T_ID); \r\n" + 
					"ALTER TABLE [HOLDING_HISTORY] ADD CONSTRAINT FK_HOLDING_HISTORY_HH_H_T_ID_TRADE_T_ID FOREIGN KEY (HH_H_T_ID) REFERENCES TRADE(T_ID);\r\n" + 
					"ALTER TABLE [HOLDING_HISTORY] ADD CONSTRAINT FK_HOLDING_HISTORY_HH_T_ID_TRADE_T_ID FOREIGN KEY (HH_T_ID) REFERENCES TRADE(T_ID);\r\n";

			
			case "TRADE_REQUEST": return "CREATE TABLE [TRADE_REQUEST] (\r\n" + 
					"	[TR_T_ID] bigint Not Null,\r\n" + 
					"	[TR_TT_ID] CHAR(3) Not Null,\r\n" + 
					"	[TR_S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[TR_QTY] int Not Null,\r\n" + 
					"	[TR_BID_PRICE] decimal(10,2) Not Null,\r\n" + 
					"	[TR_B_ID] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [TRADE_REQUEST] ADD CONSTRAINT TRADE_REQUEST_PK PRIMARY KEY (TR_T_ID); \r\n" + 
					"ALTER TABLE [TRADE_REQUEST] ADD CONSTRAINT FK_TRADE_REQUEST_TR_T_ID_TRADE_T_ID FOREIGN KEY (TR_T_ID) REFERENCES TRADE(T_ID);\r\n" + 
					"ALTER TABLE [TRADE_REQUEST] ADD CONSTRAINT FK_TRADE_REQUEST_TR_TT_ID_TRADE_TYPE_TT_ID FOREIGN KEY (TR_TT_ID) REFERENCES TRADE_TYPE(TT_ID);\r\n" + 
					"ALTER TABLE [TRADE_REQUEST] ADD CONSTRAINT FK_TRADE_REQUEST_TR_S_SYMB_SECURITY_S_SYMB FOREIGN KEY (TR_S_SYMB) REFERENCES SECURITY(S_SYMB);\r\n" + 
					"ALTER TABLE [TRADE_REQUEST] ADD CONSTRAINT FK_TRADE_REQUEST_TR_B_ID_BROKER_B_ID FOREIGN KEY (TR_B_ID) REFERENCES BROKER(B_ID);\r\n";

			
			case "TRADE": return "CREATE TABLE [TRADE] (\r\n" + 
					"	[T_ID] bigint Not Null,\r\n" + 
					"	[T_DTS] DATETIME Not Null,\r\n" + 
					"	[T_ST_ID] CHAR(4) Not Null,\r\n" + 
					"	[T_TT_ID] CHAR(3) Not Null,\r\n" + 
					"	[T_IS_CASH] bit Not Null,\r\n" + 
					"	[T_S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[T_QTY] int Not Null,\r\n" + 
					"	[T_BID_PRICE] decimal(10,2) Not Null,\r\n" + 
					"	[T_CA_ID] bigint Not Null,\r\n" + 
					"	[T_EXEC_NAME] CHAR(49) Not Null,\r\n" + 
					"	[T_TRADE_PRICE] decimal(10,2) ,\r\n" + 
					"	[T_CHRG] decimal(15,2) Not Null,\r\n" + 
					"	[T_COMM] decimal(15,2) Not Null,\r\n" + 
					"	[T_TAX] decimal(15,2) Not Null,\r\n" + 
					"	[T_LIFO] bit Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [TRADE] ADD CONSTRAINT TRADE_PK PRIMARY KEY (T_ID); \r\n" + 
					"ALTER TABLE [TRADE] ADD CONSTRAINT FK_TRADE_T_ST_ID_STATUS_TYPE_ST_ID FOREIGN KEY (T_ST_ID) REFERENCES STATUS_TYPE(ST_ID);\r\n" + 
					"ALTER TABLE [TRADE] ADD CONSTRAINT FK_TRADE_T_TT_ID_TRADE_TYPE_TT_ID FOREIGN KEY (T_TT_ID) REFERENCES TRADE_TYPE(TT_ID);\r\n" + 
					"ALTER TABLE [TRADE] ADD CONSTRAINT FK_TRADE_T_S_SYMB_SECURITY_S_SYMB FOREIGN KEY (T_S_SYMB) REFERENCES SECURITY(S_SYMB);\r\n" + 
					"ALTER TABLE [TRADE] ADD CONSTRAINT FK_TRADE_T_CA_ID_CUSTOMER_ACCOUNT_CA_ID FOREIGN KEY (T_CA_ID) REFERENCES CUSTOMER_ACCOUNT(CA_ID);\r\n";

			
			case "ACCOUNT_PERMISSION": return "CREATE TABLE [ACCOUNT_PERMISSION] (\r\n" + 
					"	[AP_CA_ID] bigint Not Null,\r\n" + 
					"	[AP_ACL] CHAR(4) Not Null,\r\n" + 
					"	[AP_TAX_ID] CHAR(20) Not Null,\r\n" + 
					"	[AP_L_NAME] CHAR(25) Not Null,\r\n" + 
					"	[AP_F_NAME] CHAR(20) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [ACCOUNT_PERMISSION] ADD CONSTRAINT ACCOUNT_PERMISSION_PK PRIMARY KEY (AP_CA_ID,AP_TAX_ID); \r\n" + 
					"ALTER TABLE [ACCOUNT_PERMISSION] ADD CONSTRAINT FK_ACCOUNT_PERMISSION_AP_CA_ID_CUSTOMER_ACCOUNT_CA_ID FOREIGN KEY (AP_CA_ID) REFERENCES CUSTOMER_ACCOUNT(CA_ID);\r\n" ;
			
			case "HOLDING_SUMMARY": return "CREATE TABLE [HOLDING_SUMMARY] (\r\n" + 
					"	[HS_CA_ID] bigint Not Null,\r\n" + 
					"	[HS_S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[HS_QTY] int Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [HOLDING_SUMMARY] ADD CONSTRAINT HOLDING_SUMMARY_PK PRIMARY KEY (HS_CA_ID,HS_S_SYMB); \r\n" + 
					"ALTER TABLE [HOLDING_SUMMARY] ADD CONSTRAINT FK_HOLDING_SUMMARY_HS_CA_ID_CUSTOMER_ACCOUNT_CA_ID FOREIGN KEY (HS_CA_ID) REFERENCES CUSTOMER_ACCOUNT(CA_ID);\r\n" + 
					"ALTER TABLE [HOLDING_SUMMARY] ADD CONSTRAINT FK_HOLDING_SUMMARY_HS_S_SYMB_SECURITY_S_SYMB FOREIGN KEY (HS_S_SYMB) REFERENCES SECURITY(S_SYMB);\r\n";
			
			
			case "DAILY_MARKET": return "CREATE TABLE [DAILY_MARKET] (\r\n" + 
					"	[DM_DATE] DATE Not Null,\r\n" + 
					"	[DM_S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[DM_CLOSE] decimal(10,2) Not Null,\r\n" + 
					"	[DM_HIGH] decimal(10,2) Not Null,\r\n" + 
					"	[DM_LOW] decimal(10,2) Not Null,\r\n" + 
					"	[DM_VOL] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [DAILY_MARKET] ADD CONSTRAINT DAILY_MARKET_PK PRIMARY KEY (DM_DATE,DM_S_SYMB); \r\n" + 
					"ALTER TABLE [DAILY_MARKET] ADD CONSTRAINT FK_DAILY_MARKET_DM_S_SYMB_SECURITY_S_SYMB FOREIGN KEY (DM_S_SYMB) REFERENCES SECURITY(S_SYMB);\r\n";

			
			case "WATCH_ITEM": return "CREATE TABLE [WATCH_ITEM] (\r\n" + 
					"	[WI_WL_ID] bigint Not Null,\r\n" + 
					"	[WI_S_SYMB] CHAR(15) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [WATCH_ITEM] ADD CONSTRAINT WATCH_ITEM_PK PRIMARY KEY (WI_WL_ID,WI_S_SYMB); \r\n" + 
					"ALTER TABLE [WATCH_ITEM] ADD CONSTRAINT FK_WATCH_ITEM_WI_WL_ID_WATCH_LIST_WL_ID FOREIGN KEY (WI_WL_ID) REFERENCES WATCH_LIST(WL_ID);\r\n" + 
					"ALTER TABLE [WATCH_ITEM] ADD CONSTRAINT FK_WATCH_ITEM_WI_S_SYMB_SECURITY_S_SYMB FOREIGN KEY (WI_S_SYMB) REFERENCES SECURITY(S_SYMB);\r\n";

			
			case "CUSTOMER_TAXRATE": return "CREATE TABLE [CUSTOMER_TAXRATE] (\r\n" + 
					"	[CX_TX_ID] CHAR(4) Not Null,\r\n" + 
					"	[CX_C_ID] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [CUSTOMER_TAXRATE] ADD CONSTRAINT CUSTOMER_TAXRATE_PK PRIMARY KEY (CX_TX_ID,CX_C_ID); \r\n" + 
					"ALTER TABLE [CUSTOMER_TAXRATE] ADD CONSTRAINT FK_CUSTOMER_TAXRATE_CX_TX_ID_TAXRATE_TX_ID FOREIGN KEY (CX_TX_ID) REFERENCES TAXRATE(TX_ID);\r\n" + 
					"ALTER TABLE [CUSTOMER_TAXRATE] ADD CONSTRAINT FK_CUSTOMER_TAXRATE_CX_C_ID_CUSTOMER_C_ID FOREIGN KEY (CX_C_ID) REFERENCES CUSTOMER(C_ID);\r\n";

			
			
			case "FINANCIAL": return "CREATE TABLE [FINANCIAL] (\r\n" + 
					"	[FI_CO_ID] bigint Not Null,\r\n" + 
					"	[FI_YEAR] smallint Not Null,\r\n" + 
					"	[FI_QTR] smallint Not Null,\r\n" + 
					"	[FI_QTR_START_DATE] DATE Not Null,\r\n" + 
					"	[FI_REVENUE] decimal(15,2) Not Null,\r\n" + 
					"	[FI_NET_EARN] decimal(15,2) Not Null,\r\n" + 
					"	[FI_BASIC_EPS] decimal(15,2) Not Null,\r\n" + 
					"	[FI_DILUT_EPS] decimal(15,2) Not Null,\r\n" + 
					"	[FI_MARGIN] decimal(15,2) Not Null,\r\n" + 
					"	[FI_INVENTORY] decimal(15,2) Not Null,\r\n" + 
					"	[FI_ASSETS] decimal(15,2) Not Null,\r\n" + 
					"	[FI_LIABILITY] decimal(15,2) Not Null,\r\n" + 
					"	[FI_OUT_BASIC] bigint Not Null,\r\n" + 
					"	[FI_OUT_DILUT] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [FINANCIAL] ADD CONSTRAINT FINANCIAL_PK PRIMARY KEY (FI_CO_ID,FI_YEAR,FI_QTR); \r\n" + 
					"ALTER TABLE [FINANCIAL] ADD CONSTRAINT FK_FINANCIAL_FI_CO_ID_COMPANY_CO_ID FOREIGN KEY (FI_CO_ID) REFERENCES COMPANY(CO_ID);\r\n";

			
			case "NEWS_XREF": return "CREATE TABLE [NEWS_XREF] (\r\n" + 
					"	[NX_NI_ID] bigint Not Null,\r\n" + 
					"	[NX_CO_ID] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [NEWS_XREF] ADD CONSTRAINT NEWS_XREF_PK PRIMARY KEY (NX_NI_ID,NX_CO_ID); \r\n" + 
					"ALTER TABLE [NEWS_XREF] ADD CONSTRAINT FK_NEWS_XREF_NX_NI_ID_NEWS_ITEM_NI_ID FOREIGN KEY (NX_NI_ID) REFERENCES NEWS_ITEM(NI_ID);\r\n" + 
					"ALTER TABLE [NEWS_XREF] ADD CONSTRAINT FK_NEWS_XREF_NX_CO_ID_COMPANY_CO_ID FOREIGN KEY (NX_CO_ID) REFERENCES COMPANY(CO_ID);\r\n";
			

			case "LAST_TRADE": return "CREATE TABLE [LAST_TRADE] (\r\n" + 
					"	[LT_S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[LT_DTS] DATETIME Not Null,\r\n" + 
					"	[LT_PRICE] decimal(10,2) Not Null,\r\n" + 
					"	[LT_OPEN_PRICE] decimal(10,2) Not Null,\r\n" + 
					"	[LT_VOL] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [LAST_TRADE] ADD CONSTRAINT LAST_TRADE_PK PRIMARY KEY (LT_S_SYMB); \r\n" + 
					"ALTER TABLE [LAST_TRADE] ADD CONSTRAINT FK_LAST_TRADE_LT_S_SYMB_SECURITY_S_SYMB FOREIGN KEY (LT_S_SYMB) REFERENCES SECURITY(S_SYMB);\r\n";
			
			
			case "CUSTOMER_ACCOUNT": return "CREATE TABLE [CUSTOMER_ACCOUNT] (\r\n" + 
					"	[CA_ID] bigint Not Null,\r\n" + 
					"	[CA_B_ID] bigint Not Null,\r\n" + 
					"	[CA_C_ID] bigint Not Null,\r\n" + 
					"	[CA_NAME] CHAR(50) ,\r\n" + 
					"	[CA_TAX_ST] smallint Not Null,\r\n" + 
					"	[CA_BAL] decimal(12,2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [CUSTOMER_ACCOUNT] ADD CONSTRAINT CUSTOMER_ACCOUNT_PK PRIMARY KEY (CA_ID); \r\n" + 
					"ALTER TABLE [CUSTOMER_ACCOUNT] ADD CONSTRAINT FK_CUSTOMER_ACCOUNT_CA_B_ID_BROKER_B_ID FOREIGN KEY (CA_B_ID) REFERENCES BROKER(B_ID);\r\n" + 
					"ALTER TABLE [CUSTOMER_ACCOUNT] ADD CONSTRAINT FK_CUSTOMER_ACCOUNT_CA_C_ID_CUSTOMER_C_ID FOREIGN KEY (CA_C_ID) REFERENCES CUSTOMER(C_ID);\r\n";

			
			case "COMPANY_COMPETITOR": return "CREATE TABLE [COMPANY_COMPETITOR] (\r\n" + 
					"	[CP_CO_ID] bigint Not Null,\r\n" + 
					"	[CP_COMP_CO_ID] bigint Not Null,\r\n" + 
					"	[CP_IN_ID] CHAR(2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [COMPANY_COMPETITOR] ADD CONSTRAINT COMPANY_COMPETITOR_PK PRIMARY KEY (CP_CO_ID,CP_COMP_CO_ID,CP_IN_ID); \r\n" + 
					"ALTER TABLE [COMPANY_COMPETITOR] ADD CONSTRAINT FK_COMPANY_COMPETITOR_CP_CO_ID_COMPANY_CO_ID FOREIGN KEY (CP_CO_ID) REFERENCES COMPANY(CO_ID);\r\n" + 
					"ALTER TABLE [COMPANY_COMPETITOR] ADD CONSTRAINT FK_COMPANY_COMPETITOR_CP_COMP_CO_ID_COMPANY_CO_ID FOREIGN KEY (CP_COMP_CO_ID) REFERENCES COMPANY(CO_ID);\r\n" + 
					"ALTER TABLE [COMPANY_COMPETITOR] ADD CONSTRAINT FK_COMPANY_COMPETITOR_CP_IN_ID_INDUSTRY_IN_ID FOREIGN KEY (CP_IN_ID) REFERENCES INDUSTRY(IN_ID);\r\n";

			
			case "SECURITY": return "CREATE TABLE [SECURITY] (\r\n" + 
					"	[S_SYMB] CHAR(15) Not Null,\r\n" + 
					"	[S_ISSUE] CHAR(6) Not Null,\r\n" + 
					"	[S_ST_ID] CHAR(4) Not Null,\r\n" + 
					"	[S_NAME] CHAR(70) Not Null,\r\n" + 
					"	[S_EX_ID] CHAR(6) Not Null,\r\n" + 
					"	[S_CO_ID] bigint Not Null,\r\n" + 
					"	[S_NUM_OUT] bigint Not Null,\r\n" + 
					"	[S_START_DATE] DATE Not Null,\r\n" + 
					"	[S_EXCH_DATE] DATE Not Null,\r\n" + 
					"	[S_PE] decimal(15,2) Not Null,\r\n" + 
					"	[S_52WK_HIGH] decimal(10,2) Not Null,\r\n" + 
					"	[S_52WK_HIGH_DATE] DATE Not Null,\r\n" + 
					"	[S_52WK_LOW] decimal(10,2) Not Null,\r\n" + 
					"	[S_52WK_LOW_DATE] DATE Not Null,\r\n" + 
					"	[S_DIVIDEND] decimal(15,2) Not Null,\r\n" + 
					"	[S_YIELD] decimal(5,2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [SECURITY] ADD CONSTRAINT SECURITY_PK PRIMARY KEY (S_SYMB); \r\n" + 
					"ALTER TABLE [SECURITY] ADD CONSTRAINT FK_SECURITY_S_ST_ID_STATUS_TYPE_ST_ID FOREIGN KEY (S_ST_ID) REFERENCES STATUS_TYPE(ST_ID);\r\n" + 
					"ALTER TABLE [SECURITY] ADD CONSTRAINT FK_SECURITY_S_EX_ID_EXCHANGE_EX_ID FOREIGN KEY (S_EX_ID) REFERENCES EXCHANGE(EX_ID);\r\n" + 
					"ALTER TABLE [SECURITY] ADD CONSTRAINT FK_SECURITY_S_CO_ID_COMPANY_CO_ID FOREIGN KEY (S_CO_ID) REFERENCES COMPANY(CO_ID);\r\n";

			
			case "COMPANY": return "CREATE TABLE [COMPANY] (\r\n" + 
					"	[CO_ID] bigint Not Null,\r\n" + 
					"	[CO_ST_ID] CHAR(4) Not Null,\r\n" + 
					"	[CO_NAME] CHAR(60) Not Null,\r\n" + 
					"	[CO_IN_ID] CHAR(2) Not Null,\r\n" + 
					"	[CO_SP_RATE] CHAR(4) Not Null,\r\n" + 
					"	[CO_CEO] CHAR(46) Not Null,\r\n" + 
					"	[CO_AD_ID] bigint Not Null,\r\n" + 
					"	[CO_DESC] CHAR(150) Not Null,\r\n" + 
					"	[CO_OPEN_DATE] DATE Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [COMPANY] ADD CONSTRAINT COMPANY_PK PRIMARY KEY (CO_ID); \r\n" + 
					"ALTER TABLE [COMPANY] ADD CONSTRAINT FK_COMPANY_CO_ST_ID_STATUS_TYPE_ST_ID FOREIGN KEY (CO_ST_ID) REFERENCES STATUS_TYPE(ST_ID);\r\n" + 
					"ALTER TABLE [COMPANY] ADD CONSTRAINT FK_COMPANY_CO_IN_ID_INDUSTRY_IN_ID FOREIGN KEY (CO_IN_ID) REFERENCES INDUSTRY(IN_ID);\r\n" + 
					"ALTER TABLE [COMPANY] ADD CONSTRAINT FK_COMPANY_CO_AD_ID_ADDRESS_AD_ID FOREIGN KEY (CO_AD_ID) REFERENCES ADDRESS(AD_ID);\r\n";

			
			
			case "COMMISSION_RATE": return "CREATE TABLE [COMMISSION_RATE] (\r\n" + 
					"	[CR_C_TIER] smallint Not Null,\r\n" + 
					"	[CR_TT_ID] CHAR(3) Not Null,\r\n" + 
					"	[CR_EX_ID] CHAR(6) Not Null,\r\n" + 
					"	[CR_FROM_QTY] int Not Null,\r\n" + 
					"	[CR_TO_QTY] int Not Null,\r\n" + 
					"	[CR_RATE] decimal(5,2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [COMMISSION_RATE] ADD CONSTRAINT COMMISSION_RATE_PK PRIMARY KEY (CR_C_TIER,CR_TT_ID,CR_EX_ID,CR_FROM_QTY); \r\n" + 
					"ALTER TABLE [COMMISSION_RATE] ADD CONSTRAINT FK_COMMISSION_RATE_CR_TT_ID_TRADE_TYPE_TT_ID FOREIGN KEY (CR_TT_ID) REFERENCES TRADE_TYPE(TT_ID);\r\n" + 
					"ALTER TABLE [COMMISSION_RATE] ADD CONSTRAINT FK_COMMISSION_RATE_CR_EX_ID_EXCHANGE_EX_ID FOREIGN KEY (CR_EX_ID) REFERENCES EXCHANGE(EX_ID);\r\n";

			
			
			case "EXCHANGE": return "CREATE TABLE [EXCHANGE] (\r\n" + 
					"	[EX_ID] CHAR(6) Not Null,\r\n" + 
					"	[EX_NAME] CHAR(100) Not Null,\r\n" + 
					"	[EX_NUM_SYMB] int Not Null,\r\n" + 
					"	[EX_OPEN] smallint Not Null,\r\n" + 
					"	[EX_CLOSE] smallint Not Null,\r\n" + 
					"	[EX_DESC] CHAR(150) ,\r\n" + 
					"	[EX_AD_ID] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [EXCHANGE] ADD CONSTRAINT EXCHANGE_PK PRIMARY KEY (EX_ID); \r\n" + 
					"ALTER TABLE [EXCHANGE] ADD CONSTRAINT FK_EXCHANGE_EX_AD_ID_ADDRESS_AD_ID FOREIGN KEY (EX_AD_ID) REFERENCES ADDRESS(AD_ID);\r\n";


			case "WATCH_LIST": return "CREATE TABLE [WATCH_LIST] (\r\n" + 
					"	[WL_ID] bigint Not Null,\r\n" + 
					"	[WL_C_ID] bigint Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [WATCH_LIST] ADD CONSTRAINT WATCH_LIST_PK PRIMARY KEY (WL_ID); \r\n" + 
					"ALTER TABLE [WATCH_LIST] ADD CONSTRAINT FK_WATCH_LIST_WL_C_ID_CUSTOMER_C_ID FOREIGN KEY (WL_C_ID) REFERENCES CUSTOMER(C_ID);\r\n";

			
			case "CUSTOMER": return "CREATE TABLE [CUSTOMER] (\r\n" + 
					"	[C_ID] bigint Not Null,\r\n" + 
					"	[C_TAX_ID] CHAR(20) Not Null,\r\n" + 
					"	[C_ST_ID] CHAR(4) Not Null,\r\n" + 
					"	[C_L_NAME] CHAR(25) Not Null,\r\n" + 
					"	[C_F_NAME] CHAR(20) Not Null,\r\n" + 
					"	[C_M_NAME] CHAR(1) ,\r\n" + 
					"	[C_GNDR] CHAR(1) ,\r\n" + 
					"	[C_TIER] smallint Not Null,\r\n" + 
					"	[C_DOB] DATE Not Null,\r\n" + 
					"	[C_AD_ID] bigint Not Null,\r\n" + 
					"	[C_CTRY_1] CHAR(3) ,\r\n" + 
					"	[C_AREA_1] CHAR(3) ,\r\n" + 
					"	[C_LOCAL_1] CHAR(10) ,\r\n" + 
					"	[C_EXT_1] CHAR(5) ,\r\n" + 
					"	[C_CTRY_2] CHAR(3) ,\r\n" + 
					"	[C_AREA_2] CHAR(3) ,\r\n" + 
					"	[C_LOCAL_2] CHAR(10) ,\r\n" + 
					"	[C_EXT_2] CHAR(5) ,\r\n" + 
					"	[C_CTRY_3] CHAR(3) ,\r\n" + 
					"	[C_AREA_3] CHAR(3) ,\r\n" + 
					"	[C_LOCAL_3] CHAR(10) ,\r\n" + 
					"	[C_EXT_3] CHAR(5) ,\r\n" + 
					"	[C_EMAIL_1] CHAR(50) ,\r\n" + 
					"	[C_EMAIL_2] CHAR(50) \r\n" + 
					");\r\n" + 
					"ALTER TABLE [CUSTOMER] ADD CONSTRAINT CUSTOMER_PK PRIMARY KEY (C_ID); \r\n" + 
					"ALTER TABLE [CUSTOMER] ADD CONSTRAINT FK_CUSTOMER_C_ST_ID_STATUS_TYPE_ST_ID FOREIGN KEY (C_ST_ID) REFERENCES STATUS_TYPE(ST_ID);\r\n" + 
					"ALTER TABLE [CUSTOMER] ADD CONSTRAINT FK_CUSTOMER_C_AD_ID_ADDRESS_AD_ID FOREIGN KEY (C_AD_ID) REFERENCES ADDRESS(AD_ID);\r\n";

			
			case "ADDRESS": return "CREATE TABLE [ADDRESS] (\r\n" + 
					"	[AD_ID] bigint Not Null,\r\n" + 
					"	[AD_LINE1] CHAR(80) ,\r\n" + 
					"	[AD_LINE2] CHAR(80) ,\r\n" + 
					"	[AD_ZC_CODE] CHAR(12) Not Null,\r\n" + 
					"	[AD_CTRY] CHAR(80) \r\n" + 
					");\r\n" + 
					"ALTER TABLE [ADDRESS] ADD CONSTRAINT ADDRESS_PK PRIMARY KEY (AD_ID); \r\n" + 
					"ALTER TABLE [ADDRESS] ADD CONSTRAINT FK_ADDRESS_AD_ZC_CODE_ZIP_CODE_ZC_CODE FOREIGN KEY (AD_ZC_CODE) REFERENCES ZIP_CODE(ZC_CODE);\r\n";

			
			case "ZIP_CODE": return "CREATE TABLE [ZIP_CODE] (\r\n" + 
					"	[ZC_CODE] CHAR(12) Not Null,\r\n" + 
					"	[ZC_TOWN] CHAR(80) Not Null,\r\n" + 
					"	[ZC_DIV] CHAR(80) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [ZIP_CODE] ADD CONSTRAINT ZIP_CODE_PK PRIMARY KEY (ZC_CODE); \r\n";
			
			
			case "TAXRATE": return "CREATE TABLE [TAXRATE] (\r\n" + 
					"	[TX_ID] CHAR(4) Not Null,\r\n" + 
					"	[TX_NAME] CHAR(50) Not Null,\r\n" + 
					"	[TX_RATE] decimal(6,5) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [TAXRATE] ADD CONSTRAINT TAXRATE_PK PRIMARY KEY (TX_ID); \r\n";			
			
			case "BROKER": return "CREATE TABLE [BROKER] (\r\n" + 
					"	[B_ID] bigint Not Null,\r\n" + 
					"	[B_ST_ID] CHAR(4) Not Null,\r\n" + 
					"	[B_NAME] CHAR(49) Not Null,\r\n" + 
					"	[B_NUM_TRADES] int Not Null,\r\n" + 
					"	[B_COMM_TOTAL] decimal(12,2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [BROKER] ADD CONSTRAINT BROKER_PK PRIMARY KEY (B_ID); \r\n" + 
					"ALTER TABLE [BROKER] ADD CONSTRAINT FK_BROKER_B_ST_ID_STATUS_TYPE_ST_ID FOREIGN KEY (B_ST_ID) REFERENCES STATUS_TYPE(ST_ID);\r\n";
			
			
			case "STATUS_TYPE": return "CREATE TABLE [STATUS_TYPE] (\r\n" + 
					"	[ST_ID] CHAR(4) Not Null,\r\n" + 
					"	[ST_NAME] CHAR(10) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [STATUS_TYPE] ADD CONSTRAINT STATUS_TYPE_PK PRIMARY KEY (ST_ID); \r\n";
			
			
			case "INDUSTRY": return "CREATE TABLE [INDUSTRY] (\r\n" + 
					"	[IN_ID] CHAR(2) Not Null,\r\n" + 
					"	[IN_NAME] CHAR(50) Not Null,\r\n" + 
					"	[IN_SC_ID] CHAR(2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [INDUSTRY] ADD CONSTRAINT INDUSTRY_PK PRIMARY KEY (IN_ID); \r\n" + 
					"ALTER TABLE [INDUSTRY] ADD CONSTRAINT FK_INDUSTRY_IN_SC_ID_SECTOR_SC_ID FOREIGN KEY (IN_SC_ID) REFERENCES SECTOR(SC_ID);\r\n";
			
			
			case "SECTOR": return "CREATE TABLE [SECTOR] (\r\n" + 
					"	[SC_ID] CHAR(2) Not Null,\r\n" + 
					"	[SC_NAME] CHAR(30) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [SECTOR] ADD CONSTRAINT SECTOR_PK PRIMARY KEY (SC_ID); \r\n";
			
			
			case "NEWS_ITEM": return "CREATE TABLE [NEWS_ITEM] (\r\n" + 
					"	[NI_ID] bigint Not Null,\r\n" + 
					"	[NI_HEADLINE] CHAR(80) Not Null,\r\n" + 
					"	[NI_SUMMARY] CHAR(255) Not Null,\r\n" + 
					"	[NI_ITEM] varchar(MAX) Not Null,\r\n" + 
					"	[NI_DTS] DATETIME Not Null,\r\n" + 
					"	[NI_SOURCE] CHAR(30) Not Null,\r\n" + 
					"	[NI_AUTHOR] CHAR(30) \r\n" + 
					");\r\n" + 
					"ALTER TABLE [NEWS_ITEM] ADD CONSTRAINT NEWS_ITEM_PK PRIMARY KEY (NI_ID); \r\n";
			
			
			case "CHARGE": return "CREATE TABLE [CHARGE] (\r\n" + 
					"	[CH_TT_ID] CHAR(3) Not Null,\r\n" + 
					"	[CH_C_TIER] smallint Not Null,\r\n" + 
					"	[CH_CHRG] decimal(15,2) Not Null\r\n" + 
					");\r\n" + 
					"ALTER TABLE [CHARGE] ADD CONSTRAINT CHARGE_PK PRIMARY KEY (CH_TT_ID,CH_C_TIER); \r\n" + 
					"ALTER TABLE [CHARGE] ADD CONSTRAINT FK_CHARGE_CH_TT_ID_TRADE_TYPE_TT_ID FOREIGN KEY (CH_TT_ID) REFERENCES TRADE_TYPE(TT_ID);\r\n";

			
			
			case "TRADE_TYPE": return "CREATE TABLE [TRADE_TYPE] (\r\n" + 
				"	[TT_ID] CHAR(3) Not Null,\r\n" + 
				"	[TT_NAME] CHAR(12) Not Null,\r\n" + 
				"	[TT_IS_SELL] bit Not Null,\r\n" + 
				"	[TT_IS_MRKT] bit Not Null\r\n" + 
				");\r\n" + 
				"ALTER TABLE [TRADE_TYPE] ADD CONSTRAINT TRADE_TYPE_PK PRIMARY KEY (TT_ID); \r\n"; 
			
			default: return null;	
		}
		
	}

	
}
