package com.cognizant.transactionservice.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.cognizant.transactionservice.models.Transaction;
import com.cognizant.transactionservice.repository.TransactionRepository;
import com.cognizant.transactionservice.service.TransactionService;
import com.cognizant.transactionservice.service.TransactionServiceInterface;
import com.cognizant.transactionservice.util.AccountInput;
import com.cognizant.transactionservice.util.TransactionInput;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TransactionRestController {
	
	@Autowired
	TransactionRepository transRepo;

	@Autowired
	TransactionService transactionService;

	/**
	 * Method for making a deposit in a account
	 * @param token
	 * @param accountInput
	 * @return TransactionStatus - Response Entity OK if transaction was successful
	 */
	@PostMapping(value = "/deposit")
	public ResponseEntity<?> makeDeposit(@RequestHeader("Authorization") String token,
			@Valid @RequestBody AccountInput accountInput) {
		transactionService.makeDeposit(token, accountInput);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
	
	/**
	 * Method for withdrawing input amount from an account
	 * @param token
	 * @param accountInput
	 * @return TransactionStatus - boolean flag depicting if the transaction was successful
	 */
	@PostMapping(value = "/withdraw")
	public boolean makeWithdraw(@RequestHeader("Authorization") String token,
			@Valid @RequestBody AccountInput accountInput){
		transactionService.makeWithdraw(token, accountInput);
		return true;
	}
	
	/**
	 * Method transfers amount from one account to another
	 * @param token
	 * @param TransactionInput
	 * @return TransactionStatus - boolean flag depicting if the transfer was successful
	 */
	@PostMapping(value = "/transactions")
	public boolean makeTransfer(@RequestHeader("Authorization") String token,
			@Valid @RequestBody TransactionInput transactionInput) {
		log.info("inside transaction method");
		if (transactionInput != null) {
			return transactionService.makeTransfer(token, transactionInput);
			
		} else {
			return false;
		}
	}

	

	/**
	 * To get all the transactions done in one account
	 * @param token
	 * @param accountId
	 * @return List of Transactions
	 */
	@GetMapping(value = "/getAllTransByAccId/{id}")
	public List<Transaction> getTransactionsByAccId(@RequestHeader("Authorization") String token,
			@PathVariable("id") long accId) {
		return transactionService.getTransactionsByAccId(accId,accId);
	}

	/**
	 * To get all the transactions done in one account
	 * @param token
	 * @param accountInput
	 * @return Service charges made
	 */
	
	@PostMapping(value = "/servicecharge")
	public boolean makeServiceCharges(@RequestHeader("Authorization") String token,
			@Valid @RequestBody AccountInput accountInput) {
		return transactionService.makeServiceCharges(token, accountInput);
	}

	
	

}
