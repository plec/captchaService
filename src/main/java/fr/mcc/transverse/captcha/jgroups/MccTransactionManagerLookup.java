package fr.mcc.transverse.captcha.jgroups;

import javax.transaction.TransactionManager;

import org.jboss.cache.transaction.BatchModeTransactionManager;
import org.jboss.cache.transaction.TransactionManagerLookup;

public class MccTransactionManagerLookup implements TransactionManagerLookup {
	public TransactionManager getTransactionManager() throws Exception {
		// Don't call BatchModeTransactionManager.getInstance(), which tries
		// to bind a singleton in JNDI -- just create one for our use
		return new BatchModeTransactionManager();
	}
}
