package com.cts.retailbankingsystem.rules.feign;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.cts.retailbankingsystem.rules.model.Account;

@FeignClient(name = "Account", url = "${account.URL}")
public interface AccountClient { 
	@GetMapping(value = "/getAccounts")
	public List<Account> getCustomerAccounts(@RequestHeader("Authorization") String token);
	
	@PostMapping(value = "/applyServiceCharges")
	public void applyServiceCharges(@RequestHeader("Authorization") String token,
			@RequestParam long accountId, @RequestParam float serviceCharge);
	
}	
