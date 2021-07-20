package com.cognizant.transactionservice.service;

import java.util.List;

import com.cognizant.transactionservice.models.Transaction;
import com.cognizant.transactionservice.util.AccountInput;
import com.cognizant.transactionservice.util.TransactionInput;

public interface TransactionServiceInterface {

	public boolean makeTransfer(String token, TransactionInput transactionInput);

	public boolean makeWithdraw(String token, AccountInput accountInput);

	public boolean makeDeposit(String token, AccountInput accountInput);
	
	public boolean makeServiceCharges(String token, AccountInput accountInput);
	
	public List<Transaction> getTransactionsByAccId(long accId, long accId2);
}
