#include "MEESUTImpl.h"
#include<iostream>
using namespace std;

bool MEESUTImpl::TradeResult( PTradeResultTxnInput pTxnInput ) {
	
	cout << endl << pTxnInput->trade_id << endl;
	return true;
}

bool MEESUTImpl::MarketFeed( PMarketFeedTxnInput pTxnInput ) {
	cout << endl << pTxnInput->unique_symbols << endl;
	return true;
}