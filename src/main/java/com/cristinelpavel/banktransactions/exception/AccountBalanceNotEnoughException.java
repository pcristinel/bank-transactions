package com.cristinelpavel.banktransactions.exception;

public class AccountBalanceNotEnoughException extends RuntimeException{
	public AccountBalanceNotEnoughException(String message) {
		super(message);
	}
}