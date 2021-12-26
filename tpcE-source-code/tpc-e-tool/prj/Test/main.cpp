
#include "CE.h"
#include "MEE.h"
#include "DM.h"

#include "CCESUTImpl.h"
#include "MEESUTImpl.h"
#include "DMSUTImpl.h"
#include <string>
#include <stdio.h>
using namespace TPCE;
using namespace std;

int main()
{
	// dft.iConfiguredCustomerCount = 5000;    // iDefaultLoadUnitSize
	// dft.iActiveCustomerCount = 5000;        // iDefaultLoadUnitSize
	// dft.iScaleFactor = 500;                 // iScaleFactor
	// dft.iDaysOfInitialTrades = 300;         // iDaysOfInitialTrades

	/*
	CCE::CCE( CCESUTInterface *pSUT, CBaseLogger *pLogger, const DataFileManager &dfm,
        TIdent iConfiguredCustomerCount, TIdent iActiveCustomerCount,
        INT32 iScaleFactor, INT32 iDaysOfInitialTrades,
        UINT32 UniqueId,
        const PDriverCETxnSettings pDriverCETxnSettings 
	*/
	
	string dataEnumerationsDir("flat_in");

	// Internal dependencies for the SUT (System Under Test) implementation classes
	CLogFormatTab fmt;
	CEGenLogger logger(eDriverEGenLoader, 0, "txn-generation.log", &fmt);
	DataFileManager dfm(dataEnumerationsDir);
	PDriverCETxnSettings dcetSettings = new TDriverCETxnSettings();

	printf("Pre punjenja1!\n");

	try {
//		const int CONFIGURED_CUSTOMER_COUNT = 20000;
//		const int ACTIVE_CUSTOMER_COUNT = 20000;
//		const int SCALE_FACTOR = 1000;
//		const int DAYS_OF_INITIAL_TRADE = 300;

		const int CONFIGURED_CUSTOMER_COUNT = 10000;
		const int ACTIVE_CUSTOMER_COUNT = 10000;
		const int SCALE_FACTOR = 500;
		const int DAYS_OF_INITIAL_TRADE = 300;

		fstream tpceWorkload;
		//printf("Pre punjenja2!\n");
		tpceWorkload.open("../../../../../TPC-E/tpcEBanchmark/inputData/T2_130k.sql", ios::out);
		//printf("Pre punjenja3!\n");

		// Customer request generator
		CCESUTImpl * sutImpl = new CCESUTImpl(tpceWorkload);
		//("Pre punjenja3.5!\n");
		CCE c(sutImpl, &logger, dfm,
			CONFIGURED_CUSTOMER_COUNT,
			ACTIVE_CUSTOMER_COUNT,
			SCALE_FACTOR,
			DAYS_OF_INITIAL_TRADE,
			100,  // unique id
			dcetSettings);

		//printf("Pre punjenja4!\n");
		// Market request generator (out of scope)
		MEESUTImpl * meeSutImpl = new MEESUTImpl();
		CMEE m(300, meeSutImpl, &logger, dfm, 2000);

		//printf("Pre punjenja!\n");
		// DataMaintenance request generator...
		DMSUTImpl * dmSutImpl  = new DMSUTImpl(tpceWorkload);
		CDM dm(dmSutImpl, &logger, dfm, 
			CONFIGURED_CUSTOMER_COUNT, 
			ACTIVE_CUSTOMER_COUNT, 
			SCALE_FACTOR, 
			DAYS_OF_INITIAL_TRADE, 
			100 // unique id
		);
		//printf("Pre punjenja!\n");

		// Process transactions...
		int transactionCount = 0;
		//dm.DoCleanupTxn();
		cout << "Tu je";
		while (transactionCount < 1000000)
		{
			c.DoTxn();
			transactionCount++;
		}

		m.GenerateTradeResult();
		
		// Data maintenance
		while (transactionCount < 100000) 
		{
			dm.DoTxn();
			transactionCount++;
		}
		
		sutImpl->PrintStatistics();
		tpceWorkload.close();
	}
	catch (exception& e) {
		cerr << e.what() << endl;
	}

	system("pause");
	return 0;
}