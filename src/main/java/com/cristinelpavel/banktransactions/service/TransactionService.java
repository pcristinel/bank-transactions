package com.cristinelpavel.banktransactions.service;

import com.cristinelpavel.banktransactions.dto.TransactionDTO;

/**
 * @author pcristinel
 * @since 0.0.1
 */
public interface TransactionService {
	TransactionDTO saveTransaction(TransactionDTO transactionDTO);
}