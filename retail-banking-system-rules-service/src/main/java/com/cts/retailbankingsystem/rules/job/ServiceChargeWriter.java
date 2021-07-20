package com.cts.retailbankingsystem.rules.job;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import com.cts.retailbankingsystem.rules.feign.AccountClient;
import com.cts.retailbankingsystem.rules.feign.AuthClient;
import com.cts.retailbankingsystem.rules.model.Account;
import com.cts.retailbankingsystem.rules.service.RBSRulesService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceChargeWriter implements ItemWriter<Account> {

	private RBSRulesService rulesService;
	private AccountClient accountClient;
	private AuthClient authClient;

	ServiceChargeWriter(RBSRulesService rulesService, AccountClient accountClient, AuthClient authClient) {
		this.rulesService = rulesService;
		this.accountClient = accountClient;
		this.authClient = authClient;
	}

	/**
	 * Method to evaluate Minimum balance and apply service charges
	 */
	@Override
	public void write(List<? extends Account> accounts) throws Exception {
		String token = authClient.generateToken();
		float serviceCharge = rulesService.getServiceCharge();

		accounts.forEach(account -> {
			if (!rulesService.evaluateMinBalance(account.getCurrentBalance(), account.getAccountId())) {
				accountClient.applyServiceCharges(token, account.getAccountId(), serviceCharge);
				log.info("Service Charge applied as Account Balance is Low for AccountId :" + account.getAccountId());
			}
		});
	}

}
