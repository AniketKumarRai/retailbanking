package com.cts.retailbankingsystem.rules.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cts.retailbankingsystem.rules.feign.AccountClient;
import com.cts.retailbankingsystem.rules.feign.AuthClient;
import com.cts.retailbankingsystem.rules.model.Account;
import com.cts.retailbankingsystem.rules.service.RBSRulesService;

@Configuration
public class JobConfig {

	@Autowired
	AccountClient accountClient;

	@Autowired
	RBSRulesService rulesService;

	@Value("${service.charge.job.batch.chunks}")
	private int chunks;

	@Bean
	ItemReader<Account> accountsReader(AccountClient accountClient, AuthClient authClient) {
		return new AccountReader(accountClient, authClient);
	}

	@Bean
	ItemWriter<Account> accountServiceChargeCalculator(RBSRulesService rulesService, AccountClient accountClient,
			AuthClient authClient) {
		return new ServiceChargeWriter(rulesService, accountClient, authClient);
	}

	@Bean
	Step accountStep(ItemReader<Account> accountReader, ItemWriter<Account> accountServiceChargeCalculator,
			StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("accountStep").<Account, Account>chunk(chunks).reader(accountReader)
				.writer(accountServiceChargeCalculator).build();
	}

	@Bean
	Job serviceChargeJob(JobBuilderFactory jobBuilderFactory, @Qualifier("accountStep") Step accountStep) {
		return jobBuilderFactory.get("serviceChargeJob").incrementer(new RunIdIncrementer()).flow(accountStep).end()
				.build();
	}
}
