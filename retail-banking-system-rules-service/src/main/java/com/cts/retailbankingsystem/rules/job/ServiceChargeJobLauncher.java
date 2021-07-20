package com.cts.retailbankingsystem.rules.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.batch.operations.JobRestartException;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ServiceChargeJobLauncher {

	private final Job job;

	private final JobLauncher jobLauncher;

	@Autowired
	ServiceChargeJobLauncher(@Qualifier("serviceChargeJob") Job job, JobLauncher jobLauncher) {
		this.job = job;
		this.jobLauncher = jobLauncher;
	}

	/**
	 * Method to launch service charge job
	 * 
	 * @throws JobParametersInvalidException
	 * @throws JobExecutionAlreadyRunningException
	 * @throws JobRestartException
	 * @throws JobInstanceAlreadyCompleteException
	 * @throws org.springframework.batch.core.repository.JobRestartException
	 */
	@Scheduled(cron = "${service.charge.job.cron}")
	void launchServiceChargeJob()
			throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException,
			JobInstanceAlreadyCompleteException, org.springframework.batch.core.repository.JobRestartException {
		log.info("Starting Service Charge job");
		JobExecution execution = jobLauncher.run(job, newExecution());
		log.info("Stopping Service Charge job with Job RunId : " + execution.getJobId());
	}

	private JobParameters newExecution() {
		Map<String, JobParameter> parameters = new HashMap<>();
		JobParameter parameter = new JobParameter(new Date());
		parameters.put("currentTime", parameter);
		return new JobParameters(parameters);
	}

}
