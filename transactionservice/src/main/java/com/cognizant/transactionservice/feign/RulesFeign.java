package com.cognizant.transactionservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.cognizant.transactionservice.models.RulesInput;

@FeignClient(name = "rules-ms", url = "${feign.url-rule-service}")
public interface RulesFeign {

	@GetMapping("/evaluateMinBal")
	public boolean evaluateMinBal(@RequestParam double balance, @RequestParam long accountId,
			@RequestHeader(value = "Authorization", required = true) String token);

	@PostMapping("/serviceCharges")
	public ResponseEntity<?> serviceCharges(@RequestHeader("Authorization") String token,
			@RequestBody RulesInput account);

}
