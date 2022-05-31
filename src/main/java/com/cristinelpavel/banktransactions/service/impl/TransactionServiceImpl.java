package com.cristinelpavel.banktransactions.service.impl;

import com.cristinelpavel.banktransactions.dto.TransactionStatusRequestDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO.Status;
import com.cristinelpavel.banktransactions.exception.AccountBalanceNotEnoughException;
import com.cristinelpavel.banktransactions.exception.TransactionNotFoundException;
import com.cristinelpavel.banktransactions.model.Account;
import com.cristinelpavel.banktransactions.model.Transaction;
import com.cristinelpavel.banktransactions.repository.TransactionRepository;
import com.cristinelpavel.banktransactions.service.AccountService;
import com.cristinelpavel.banktransactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@RequiredArgsConstructor
@Validated
@Service
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final AccountService accountService;

	public Transaction saveTransaction(@Valid @NotNull Transaction transaction) {
		Account account = accountService.findAccountById(transaction.getAccountIban());

		if (transaction.getAmount().compareTo(BigDecimal.ZERO) < 0
						&& account.getAccountBalance().add(transaction.getAmount()).compareTo(BigDecimal.ZERO) < 0) {
			throw new AccountBalanceNotEnoughException("Transaction amount is bigger than account balance");
		}

		if (Objects.nonNull(transaction.getFee())) {
			account.setAccountBalance(account.getAccountBalance().add(transaction.getAmount()).subtract(transaction.getFee()));

			accountService.updateAccount(account);
		}

		return transactionRepository.save(transaction);
	}

	public List<Transaction> findTransactionsByAccountIban(@NotBlank String accountIban, @NotNull Sort sort) {
		Optional<List<Transaction>> transactionsList = transactionRepository.findByAccountIban(accountIban, sort);

		return transactionsList.orElseThrow(() -> new TransactionNotFoundException("No transactions found for account " + accountIban));
	}


	public TransactionStatusResponseDTO findTransactionStatus(@NotNull UUID reference,
																														@NotNull TransactionStatusRequestDTO.Channel channel) {
		Optional<Transaction> optionalTransaction = transactionRepository.findById(reference);

		if (optionalTransaction.isEmpty()) {
			return TransactionStatusResponseDTO.builder()
							.reference(reference)
							.status(Status.INVALID)
							.build();
		}

		Transaction transaction = optionalTransaction.get();

		switch (channel) {
			case CLIENT, ATM -> {
				TransactionStatusResponseDTO transactionStatusResponseDTO = new TransactionStatusResponseDTO();
				transactionStatusResponseDTO.setReference(reference);

				BigDecimal fee = transaction.getFee() != null ? transaction.getFee() : BigDecimal.ZERO;

				transactionStatusResponseDTO.setAmount(transaction.getAmount().subtract(fee));

				if (ChronoUnit.DAYS.between(transaction.getDate(), Instant.now()) > 0) {
					transactionStatusResponseDTO.setStatus(Status.SETTLED);
				} else if (ChronoUnit.DAYS.between(transaction.getDate(), Instant.now()) == 0) {
					transactionStatusResponseDTO.setStatus(Status.PENDING);
				} else {
					Status status = channel.equals(TransactionStatusRequestDTO.Channel.CLIENT) ?
									Status.FUTURE : Status.PENDING;

					transactionStatusResponseDTO.setStatus(status);
				}

				return transactionStatusResponseDTO;
			}

			case INTERNAL -> {
				TransactionStatusResponseDTO transactionStatusResponseDTO = new TransactionStatusResponseDTO();
				transactionStatusResponseDTO.setReference(reference);
				transactionStatusResponseDTO.setAmount(transaction.getAmount());
				transactionStatusResponseDTO.setFee(transaction.getFee());

				if (ChronoUnit.DAYS.between(transaction.getDate(), Instant.now()) > 0) {
					transactionStatusResponseDTO.setStatus(Status.SETTLED);
				} else if (ChronoUnit.DAYS.between(transaction.getDate(), Instant.now()) == 0) {
					transactionStatusResponseDTO.setStatus(Status.PENDING);
				} else {
					transactionStatusResponseDTO.setStatus(Status.FUTURE);
				}

				return transactionStatusResponseDTO;
			}

			default -> {
				return TransactionStatusResponseDTO.builder()
									.reference(reference)
									.status(Status.INVALID)
									.build();
			}
		}
	}
}