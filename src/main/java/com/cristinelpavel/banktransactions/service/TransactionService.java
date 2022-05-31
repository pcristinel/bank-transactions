package com.cristinelpavel.banktransactions.service;

import com.cristinelpavel.banktransactions.dto.TransactionStatusRequestDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO;
import com.cristinelpavel.banktransactions.model.Transaction;
import org.springframework.data.domain.Sort;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * @author pcristinel
 * @since 0.0.1
 */
public interface TransactionService {
	Transaction saveTransaction(@Valid @NotNull Transaction transaction);

	List<Transaction> findTransactionsByAccountIban(@NotBlank String accountIban, @NotNull Sort by);

	TransactionStatusResponseDTO findTransactionStatus(@NotNull UUID reference, @NotNull TransactionStatusRequestDTO.Channel channel);
}