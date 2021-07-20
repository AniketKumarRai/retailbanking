package com.cognizant.accountservice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.cognizant.accountservice.exceptionhandling.AccessDeniedException;
import com.cognizant.accountservice.exceptionhandling.AccountNotFoundException;
import com.cognizant.accountservice.exceptionhandling.MinimumBalanceException;
import com.cognizant.accountservice.feignclient.AuthFeignClient;
import com.cognizant.accountservice.feignclient.TransactionFeign;
import com.cognizant.accountservice.model.Account;
import com.cognizant.accountservice.model.AccountCreationStatus;
import com.cognizant.accountservice.model.AccountInput;
import com.cognizant.accountservice.model.AuthenticationResponse;
import com.cognizant.accountservice.model.Transaction;
import com.cognizant.accountservice.model.TransactionInput;
import com.cognizant.accountservice.repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
	
	/**
	 *  Class used Implementing account service -> Service Layer class
	 */
	
	@Autowired
	private AuthFeignClient authFeignClient;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TransactionFeign transactionFeign;
	
	@Autowired
	private AccountServiceImpl accountServiceImpl; // Account Service Methods Implementation


	/*
	 *  service method to create customer account
	 */
	@Override
	public AccountCreationStatus createAccount(String customerId, Account account) {
		accountRepository.save(account);
		AccountCreationStatus accountCreationStatus = new AccountCreationStatus(account.getAccountId(),"Sucessfully Created");
		log.info("Account Created Sucessfully");
		return accountCreationStatus;
	}


	/*
	 *   service method to get customer account
	 */
	@Override
	public List<Account> getCustomerAccount(String token, String customerId) {
		List<Account> accountList = accountRepository.findByCustomerId(customerId);
		for (Account acc : accountList) {
			acc.setTransactions(transactionFeign.getTransactionsByAccId(token, acc.getAccountId()));
		}
		return accountList;
	}

	
	/*
	 *  service method to get account by account id
	 */
	@Override
	public Account getAccount(long accountId) {
		Account account = accountRepository.findByAccountId(accountId);
		if (account == null) {
			throw new AccountNotFoundException("Account Does Not Exist");
		}
		return account;
	}

	/*
	 *  service method to get account by account id
	 */
	@Override
	public Account updateAccountBalance(Account account,double newBalance) {
		account.setCurrentBalance(newBalance);
		return accountRepository.save(account);
	}
	
	
	/*
	 *  service method to update balance withdraw
	 */
	@Override
	public Account updateBalance(AccountInput accountInput) {
		log.info("toUpdateAcc--->" + accountInput.getAccountId());
		Account toUpdateAcc = accountRepository.findByAccountId(accountInput.getAccountId());
		toUpdateAcc.setCurrentBalance(toUpdateAcc.getCurrentBalance() - accountInput.getAmount());
		return accountRepository.save(toUpdateAcc);
	}

	
	/*
	 *  service method to update balance deposit
	 */
	@Override
	public Account updateDepositBalance(AccountInput accountInput) {
		log.info("toUpdateAcc--->" + accountInput.getAccountId());
		Account toUpdateAcc = accountRepository.findByAccountId(accountInput.getAccountId());
		toUpdateAcc.setCurrentBalance(toUpdateAcc.getCurrentBalance() + accountInput.getAmount());
		return accountRepository.save(toUpdateAcc);
	}

	
	/*
	 *  service method to check token validity
	 */
	@Override
	public AuthenticationResponse hasPermission(String token) {
		return authFeignClient.getValidity(token);
	}

 
	/*
	 *  service method to check validity and employee permissions
	 */
	@Override
	public AuthenticationResponse hasEmployeePermission(String token) {
		AuthenticationResponse validity = authFeignClient.getValidity(token);
		if (!authFeignClient.getRole(validity.getUserid()).equals("EMPLOYEE"))
			throw new AccessDeniedException("NOT ALLOWED");
		else
			return validity;
	}

	 
	/*
	 *  service method to check validity and customer permissions
	 */
	@Override
	public AuthenticationResponse hasCustomerPermission(String token) {
		AuthenticationResponse validity = authFeignClient.getValidity(token);
		if (!authFeignClient.getRole(validity.getUserid()).equals("CUSTOMER"))
			throw new AccessDeniedException("NOT ALLOWED");
		else
			return validity;
	}
	
	@Override
	public List<Account> getAllAccounts(){
		// TODO Auto-generated method stub
		try {
			return accountRepository.findAll();			
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public Account makeDep(String token,AccountInput accInput) {
		transactionFeign.makeDeposit(token, accInput);
		Account newUpdateAccBal = accountServiceImpl.updateDepositBalance(accInput);
		List<Transaction> list = transactionFeign.getTransactionsByAccId(token, accInput.getAccountId());
		newUpdateAccBal.setTransactions(list);
		log.info("amount deposited");
		return newUpdateAccBal;
	}
	public Account makeWid(String token,AccountInput accInput) {
		try {
			transactionFeign.makeWithdraw(token, accInput);
		} catch (Exception e) {
			throw new MinimumBalanceException("Minimum Balance 1000 should be maintaind");
		}
		Account newUpdateAccBal = accountServiceImpl.updateBalance(accInput);
		List<Transaction> list = transactionFeign.getTransactionsByAccId(token, accInput.getAccountId());
		newUpdateAccBal.setTransactions(list);
		log.info("amount withdraw sucessful");
		return newUpdateAccBal;
	}
	public ResponseEntity<String> makeTransaction(String token,TransactionInput transInput) {
		boolean status = true;
		try {
			status = transactionFeign.makeTransfer(token, transInput);
		} catch (Exception e) {
			throw new MinimumBalanceException("Minimum Balance 1000 should be maintaind");
		}
		if (status == false) {
			return new ResponseEntity<>("Transaction Failed", HttpStatus.NOT_IMPLEMENTED);
		}
		Account updatedSourceAccBal = accountServiceImpl.updateBalance(transInput.getSourceAccount());
		List<Transaction> sourcelist = transactionFeign.getTransactionsByAccId(token,
				transInput.getSourceAccount().getAccountId());
		updatedSourceAccBal.setTransactions(sourcelist);

		Account updatedTargetAccBal = accountServiceImpl.updateDepositBalance(transInput.getTargetAccount());
		List<Transaction> targetlist = transactionFeign.getTransactionsByAccId(token,
				transInput.getTargetAccount().getAccountId());
		updatedTargetAccBal.setTransactions(targetlist);
		return new ResponseEntity<>(
				"Transaction Made Successfully From Source AccId" + transInput.getSourceAccount().getAccountId()
						+ " TO Target AccId " + transInput.getTargetAccount().getAccountId() + " ",
				HttpStatus.OK);
		
	}
	public void makeeServiceCharge(String token, String accountId, float serviceCharge) {
		AccountInput accInput = new AccountInput();
		long accId = Long.parseLong(accountId);
		accInput.setAccountId(accId);
		accInput.setAmount(serviceCharge);
		transactionFeign.makeServiceCharges(token, accInput);
		Account newUpdateAccBal = accountServiceImpl.updateBalance(accInput);
		List<Transaction> list = transactionFeign.getTransactionsByAccId(token, accInput.getAccountId());
		newUpdateAccBal.setTransactions(list);
		log.info("Balance after applying Service Charge:" + newUpdateAccBal.getCurrentBalance());
	}

}