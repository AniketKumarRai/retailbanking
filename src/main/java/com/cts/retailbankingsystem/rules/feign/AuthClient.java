package com.cts.retailbankingsystem.rules.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;


import com.cts.retailbankingsystem.rules.model.AuthenticationResponse;

@FeignClient(name = "auth", url = "${auth.URL}")
@Component
public interface AuthClient {
	@GetMapping("/validateToken")
	public AuthenticationResponse getValidity(@RequestHeader("Authorization") String token);

	@GetMapping("/genToken")
	public String generateToken();

}





	
