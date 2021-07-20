package com.cognizant.transactionservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.cognizant.transactionservice.models.Account;

@FeignClient(name = "account-ms", url = "${feign.url-account-service}")
public interface AccountFeign {
		
	@GetMapping("/getAccount/{accountId}")
	public Account getAccount(@RequestHeader("Authorization") String token , @PathVariable(name="accountId") long accountId);

	@PostMapping("/updateAccountBalance")
	public boolean updateAccountBalance(@RequestBody Account account,@RequestParam double currentBalance);
	

}
