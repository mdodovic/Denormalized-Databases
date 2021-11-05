select top 10 CA_ID, CA_BAL,((sum(HS_QTY * LT_PRICE))) as RES_SUM

from [tpcE].[dbo].[CUSTOMER_ACCOUNT] left outer join ([tpcE].[dbo].[HOLDING_SUMMARY] inner join [tpcE].[dbo].[LAST_TRADE] on LT_S_SYMB = HS_S_SYMB) on HS_CA_ID = CA_ID

where CA_C_ID = 4300000003

group by CA_ID, CA_BAL

order by 3 asc;
