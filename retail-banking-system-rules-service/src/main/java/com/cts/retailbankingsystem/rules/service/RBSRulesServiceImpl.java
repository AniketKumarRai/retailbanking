package com.cts.retailbankingsystem.rules.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RBSRulesServiceImpl implements RBSRulesService {

	@Value("${account.min.balance}")
	private int minBalance;

	@Value("${service.charge}")
	private float serviceCharge;

	@Override
	public boolean evaluateMinBalance(double balance, long accountId) {
		boolean status = balance < minBalance ? false : true;
		log.info("AccountId : " + accountId + " RuleStatus : " + status);
		return status;
	}

	@Override
	public float getServiceCharge() {
		log.info("Service Charge : " + serviceCharge);
		return serviceCharge;
	}

}
