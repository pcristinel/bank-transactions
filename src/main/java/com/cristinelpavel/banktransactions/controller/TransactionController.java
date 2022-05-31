package com.cristinelpavel.banktransactions.controller;

import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusRequestDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pcristinel
 * @since 0.0.1
 */
public interface TransactionController {
	ResponseEntity<TransactionDTO> createTransaction(@RequestBody @Valid TransactionDTO transactionDTO, UriComponentsBuilder uriComponentsBuilder);

	ResponseEntity<List<TransactionDTO>> findTransactionsByAccountIban(
					@RequestParam String accountIban,
					@RequestParam(required = false, defaultValue = "amount") String sortField,
					@RequestParam(required = false, defaultValue = "DESC") String sortOrder);

	ResponseEntity<TransactionStatusResponseDTO> findTransactionStatus(@RequestBody TransactionStatusRequestDTO transactionStatusRequestDTO);
}