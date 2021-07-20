package com.cts.retailbankingsystem.rules.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Account {

	private long accountId;
	private double currentBalance;

}
