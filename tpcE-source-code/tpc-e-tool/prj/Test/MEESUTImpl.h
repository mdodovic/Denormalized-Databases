#ifndef CE_MEESUTImpl_H_
#define CE_MEESUTImpl_H_

#include "MEESUTInterface.h"
#include <fstream>
#include <iostream>
using namespace std;
using namespace TPCE;

class MEESUTImpl : public CMEESUTInterface {
private:
	fstream fout;
	fstream out;
	// int bvCnt, cpCnt, mwCnt, sdCnt, tlCnt, toCnt, tsCnt, tuCnt;
public:
	MEESUTImpl() {
		cout << endl << "USLOO" << endl;
		fout.open("./xyz.txt", ios::out);
		fout << "EXEC " << "DataMaintenanceFrame1 ";
		cout << endl << "USLO2" << endl;
	}
	virtual bool TradeResult( PTradeResultTxnInput pTxnInput );
    virtual bool MarketFeed( PMarketFeedTxnInput pTxnInput );

	~MEESUTImpl() {
		fout.close();
	}
};

#endif