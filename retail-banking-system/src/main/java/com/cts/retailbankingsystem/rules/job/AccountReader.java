package com.cts.retailbankingsystem.rules.job;

import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.cts.retailbankingsystem.rules.feign.AccountClient;
import com.cts.retailbankingsystem.rules.model.Account;
import com.cts.retailbankingsystem.rules.feign.AuthClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountReader implements ItemReader<Account> {

	private int nextAccountIndex;
	private List<Account> accounts;
	private AccountClient accountClient;
	private AuthClient authClient;

	AccountReader(AccountClient accountClient, AuthClient authClient) {
		this.accountClient = accountClient;
		this.authClient = authClient;
		nextAccountIndex = 0;
	}

	/**
	 * Method to read the account details to be processed by job
	 */
	@Override
	public Account read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

		// Accounts fetch for Job Execution
		if (this.accounts == null || accounts.isEmpty()) {
			log.info("Fetching customer accounts for job processing");
			String token = authClient.generateToken();
			accounts = accountClient.getCustomerAccounts(token);
		}

		log.info("Reading the information of the next account");
		Account nextAccount = null;
		if (nextAccountIndex < accounts.size()) {
			// Reading next account from List
			nextAccount = accounts.get(nextAccountIndex);
			nextAccountIndex++;
		} else {
			// Resetting the index and accounts for next job run
			nextAccountIndex = 0;
			this.accounts = null;
		}

		log.info("Found account: " + nextAccount);

		return nextAccount;
	}

}
