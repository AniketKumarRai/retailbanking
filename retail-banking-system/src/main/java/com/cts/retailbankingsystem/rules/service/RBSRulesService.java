package com.cts.retailbankingsystem.rules.service;

public interface RBSRulesService {

	boolean evaluateMinBalance(double balance, long accountId);

	float getServiceCharge();

}
