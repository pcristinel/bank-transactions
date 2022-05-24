package com.cristinelpavel.banktransactions.controller.impl;

import com.cristinelpavel.banktransactions.controller.TransactionController;
import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionControllerImpl implements TransactionController {

	private final TransactionService transactionService;

	@PostMapping
	public ResponseEntity<TransactionDTO> createTransaction(@RequestBody @Valid TransactionDTO transactionDTO, UriComponentsBuilder uriComponentsBuilder) {
		TransactionDTO savedTransaction = transactionService.saveTransaction(transactionDTO);

		URI savedTransactionUriLocation = uriComponentsBuilder.path("/api/v1/transactions/{id}").build(savedTransaction.getReference());

		return ResponseEntity.created(savedTransactionUriLocation).body(savedTransaction);
	}
}