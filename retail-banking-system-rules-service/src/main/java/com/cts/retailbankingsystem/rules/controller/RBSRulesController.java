package com.cts.retailbankingsystem.rules.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cts.retailbankingsystem.rules.exception.AuthorizationException;
import com.cts.retailbankingsystem.rules.feign.AuthClient;
import com.cts.retailbankingsystem.rules.model.AuthenticationResponse;
import com.cts.retailbankingsystem.rules.service.RBSRulesService;

import io.swagger.annotations.ApiOperation;

@RestController
public class RBSRulesController {

	@Autowired
	private RBSRulesService service;

	@Autowired
	private AuthClient authClient;

	/**
	 * Method to evaluate minimum balance
	 * 
	 * @param balance
	 * @param accountId
	 * @param requestTokenHeader
	 * @return boolean flag to depict if account is having required minimum balance
	 */
	@ApiOperation(notes = "Returns boolean flag to depict if account is having required minimum balance", value = "Method to evaluate if account is having minimum balance based on RuleEngine")
	@GetMapping("/evaluateMinBal")
	public boolean evaluateMinBal(@RequestParam double balance, @RequestParam long accountId,
			@RequestHeader(value = "Authorization", required = true) String token) throws Exception {
		boolean rulesStatus;
		AuthenticationResponse authResponse = authClient.getValidity(token);
		if (authResponse.isValid()) {
			rulesStatus = service.evaluateMinBalance(balance, accountId);
		} else {
			throw new AuthorizationException("Unauthorized Access");
		}
		return rulesStatus;
	}

	/**
	 * Method to get the service charges
	 * 
	 * @param token
	 * @return serviceCharge
	 * @throws Exception
	 */
	@ApiOperation(notes = "Returns configured Service Charge by Rule Engine", value = "Method to get the Service Charge")
	@GetMapping("/getServiceCharges")
	public float getServiceCharges(@RequestHeader("Authorization") String token) throws Exception {
		float serviceCharge;
		AuthenticationResponse authResponse = authClient.getValidity(token);
		if (authResponse.isValid()) {
			serviceCharge = service.getServiceCharge();
		} else {
			throw new AuthorizationException("Unauthorized Access");
		}
		return serviceCharge;

	}

}
