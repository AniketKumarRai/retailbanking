package com.cognizant.authenticationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * The AuthenticationMsApplication class to start the application
 *
 */
@SpringBootApplication
@EnableSwagger2
public class AuthenticationMsApplication {

	/**
	 * The main method for app
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthenticationMsApplication.class, args);
	}

}
