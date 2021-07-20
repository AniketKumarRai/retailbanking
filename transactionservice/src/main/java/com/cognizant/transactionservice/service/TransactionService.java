package com.cognizant.transactionservice.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.transactionservice.exception.MinimumBalanceException;
import com.cognizant.transactionservice.feign.AccountFeign;
import com.cognizant.transactionservice.feign.RulesFeign;
import com.cognizant.transactionservice.models.Account;
import com.cognizant.transactionservice.models.Transaction;
import com.cognizant.transactionservice.repository.TransactionRepository;
import com.cognizant.transactionservice.util.AccountInput;
import com.cognizant.transactionservice.util.TransactionInput;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService implements TransactionServiceInterface {

	@Autowired
	private AccountFeign accountFeign;

	@Autowired
	private RulesFeign ruleFeign;

	@Autowired
	private TransactionRepository transactionRepository;

	/*
	 * Service layer method for making a deposit
	 */
	@Override
	public boolean makeDeposit(String token, AccountInput accountInput) {
		log.info("method to make a deposit");
		Account sourceAccount = accountFeign.getAccount(token, accountInput.getAccountId());
		boolean deposit = false;
		if (sourceAccount != null) {
			// Save transaction
			Transaction transaction = getTransaction(sourceAccount, sourceAccount, accountInput.getAmount(), "deposit");
			transactionRepository.save(transaction);
			deposit = true;
		}
		return deposit;
	}

	/**
	 * Service layer method for making a withdraw
	 */
	@Override
	public boolean makeWithdraw(String token, AccountInput accountInput) {
		log.info("method to make a withdraw");
		Account sourceAccount = accountFeign.getAccount(token, accountInput.getAccountId());
		double balanceAfterWithdraw = sourceAccount.getCurrentBalance() - accountInput.getAmount();
		Boolean check = (Boolean) ruleFeign.evaluateMinBal(balanceAfterWithdraw, accountInput.getAccountId(), token);
		boolean withdraw = false;
		if (check.booleanValue() == false) {
			throw new MinimumBalanceException("Minimum Balance 1000 should be maintaind");
		}

		if (sourceAccount != null) {
			// Save transaction details
			Transaction transaction = getTransaction(sourceAccount, sourceAccount, accountInput.getAmount(),
					"withdrawn");
			log.info("Transaction detail" + transaction.toString());
			transactionRepository.save(transaction);
			withdraw = true;
		}
		return withdraw;
	}

	/**
	 * Service layer method for making a transfer
	 */
	@Override
	public boolean makeTransfer(String token, TransactionInput transactionInput) throws MinimumBalanceException {

		Account sourceAccount = accountFeign.getAccount(token, transactionInput.getSourceAccount().getAccountId());
		Account targetAccount = accountFeign.getAccount(token, transactionInput.getTargetAccount().getAccountId());

		double transactionAmount = transactionInput.getAmount();
		double balanceAfterTransfer = sourceAccount.getCurrentBalance() - transactionAmount;

		boolean transfer = false;
		Boolean isValidTransaction = (Boolean) ruleFeign.evaluateMinBal(balanceAfterTransfer,
				sourceAccount.getAccountId(), token);

		if (isValidTransaction.booleanValue() == false) {
			throw new MinimumBalanceException("Minimum Balance 1000 should be maintaind");
		}

		if (sourceAccount != null && targetAccount != null) {

			// Save transaction details
			Transaction sourcetransaction = getTransaction(sourceAccount, targetAccount, transactionAmount, "transfer");
			transactionRepository.save(sourcetransaction);

			transfer = true;
		}
		return transfer;
	}

	/**
	 * Service layer method to get transaction
	 */
	private Transaction getTransaction(Account sourceAccount, Account targetAccount, double transactionAmount,
			String typeOfTransaction) {
		Transaction transaction = new Transaction();

		transaction.setAmount(transactionAmount);
		transaction.setSourceAccountId(sourceAccount.getAccountId());
		transaction.setSourceOwnerName(sourceAccount.getOwnerName());
		transaction.setTargetAccountId(targetAccount.getAccountId());
		transaction.setTargetOwnerName(targetAccount.getOwnerName());
		transaction.setInitiationDate(LocalDateTime.now());
		transaction.setReference(typeOfTransaction);
		return transaction;
	}

	/**
	 * Service layer method to make service charges
	 */
	@Override
	public List<Transaction> getTransactionsByAccId(long accId, long accId2) {
		// TODO Auto-generated method stub
		return transactionRepository.findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(accId, accId2);
	}

	@Override
	public boolean makeServiceCharges(String token, AccountInput accountInput) {
		log.info("method to make a service charges");
		Account sourceAccount = accountFeign.getAccount(token, accountInput.getAccountId());
		boolean serviceCharges = false;
		if (sourceAccount != null) {
			// Save transaction details
			Transaction transaction = getTransaction(sourceAccount, sourceAccount, accountInput.getAmount(),
					"service charge");
			transactionRepository.save(transaction);
			serviceCharges = true;
		}
		return serviceCharges;

	}

}