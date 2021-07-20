package com.cts.retailbankingsystem.rules;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableBatchProcessing
@EnableScheduling
public class RetailBankingSystemRulesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RetailBankingSystemRulesServiceApplication.class, args);
	}

}
