package com.cristinelpavel.banktransactions.controller.impl;

import com.cristinelpavel.banktransactions.controller.TransactionController;
import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusRequestDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO;
import com.cristinelpavel.banktransactions.mapper.TransactionMapper;
import com.cristinelpavel.banktransactions.model.Transaction;
import com.cristinelpavel.banktransactions.service.TransactionService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/transactions")
public class TransactionControllerImpl implements TransactionController {

	private final TransactionService transactionService;
	private final TransactionMapper transactionMapper;

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransactionDTO> createTransaction(@RequestBody @Valid TransactionDTO transactionDTO, UriComponentsBuilder uriComponentsBuilder) {
		Transaction savedTransaction = transactionService.saveTransaction(transactionMapper.toTransaction(transactionDTO));

		TransactionDTO savedTransactionDTO = transactionMapper.toDto(savedTransaction);

		URI savedTransactionUriLocation = uriComponentsBuilder.path("/api/v1/transactions/{id}").build(savedTransactionDTO.getReference());

		return ResponseEntity.created(savedTransactionUriLocation).body(savedTransactionDTO);
	}


	@GetMapping("/search")
	public ResponseEntity<List<TransactionDTO>> findTransactionsByAccountIban(
					@RequestParam("account_iban") String accountIban,
					@RequestParam(required = false, defaultValue = "amount") String sortField,
					@RequestParam(required = false, defaultValue = "DESC") String sortOrder) {

		List<Transaction> transactionsByAccountIban = transactionService.findTransactionsByAccountIban(
						accountIban,
						Sort.by(Sort.Direction.fromString(sortOrder), sortField));

		return transactionsByAccountIban.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(transactionMapper.toDtoList(transactionsByAccountIban));
	}


	@PostMapping(value = "/status", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TransactionStatusResponseDTO> findTransactionStatus(@RequestBody TransactionStatusRequestDTO transactionStatusRequestDTO) {
		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(transactionStatusRequestDTO.getReference(),
						transactionStatusRequestDTO.getChannel());

		return ResponseEntity.ok(transactionStatus);
	}
}
