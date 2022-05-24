package com.cristinelpavel.banktransactions.controller;

import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author pcristinel
 * @since 0.0.1
 */
public interface TransactionController {
	ResponseEntity<TransactionDTO> createTransaction(TransactionDTO transactionDTO, UriComponentsBuilder uriComponentsBuilder);
}