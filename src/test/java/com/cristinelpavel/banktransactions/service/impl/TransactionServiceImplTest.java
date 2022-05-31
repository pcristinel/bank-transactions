package com.cristinelpavel.banktransactions.service.impl;

import com.cristinelpavel.banktransactions.dto.TransactionStatusRequestDTO.Channel;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO.Status;
import com.cristinelpavel.banktransactions.exception.AccountBalanceNotEnoughException;
import com.cristinelpavel.banktransactions.exception.TransactionNotFoundException;
import com.cristinelpavel.banktransactions.model.Account;
import com.cristinelpavel.banktransactions.model.Transaction;
import com.cristinelpavel.banktransactions.repository.TransactionRepository;
import com.cristinelpavel.banktransactions.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class TransactionServiceImplTest {
	@InjectMocks
	private TransactionServiceImpl transactionService;

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountService accountService;

	@Test
	void withNoFee_shouldSaveTransaction_withNoFee() {
		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Account account = Account.builder()
						.accountBalance(new BigDecimal("500.25"))
						.build();

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.accountIban("ES9820385778983000760236")
						.build();

		when(accountService.findAccountById(anyString())).thenReturn(account);
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		Transaction savedTransaction = transactionService.saveTransaction(transaction);

		assertThat(savedTransaction.getFee(), is(nullValue()));
		assertThat(savedTransaction.getAmount(), is(new BigDecimal("100.25")));
	}

	@Test
	void withFee_shouldSaveTransaction_WithFee() {
		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Account account = Account.builder()
						.accountBalance(new BigDecimal("500.25"))
						.build();

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.accountIban("ES9820385778983000760236")
						.build();

		when(accountService.findAccountById(anyString())).thenReturn(account);
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		Transaction savedTransaction = transactionService.saveTransaction(transaction);

		assertThat(savedTransaction.getFee(), is(new BigDecimal("0.25")));
		assertThat(savedTransaction.getAmount(), is(new BigDecimal("100.25")));
	}

	@Test
	void shouldThrowAccountBalanceNotEnoughException() {
		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Account account = Account.builder()
						.accountBalance(new BigDecimal("500.25"))
						.build();

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("-800.25"))
						.accountIban("ES9820385778983000760236")
						.build();


		when(accountService.findAccountById(anyString())).thenReturn(account);
		when(transactionRepository.save(transaction)).thenReturn(transaction);

		assertThrows(AccountBalanceNotEnoughException.class, () -> transactionService.saveTransaction(transaction));
	}

	@Test
	void shouldFindTransactionsByAccountIban() {
		final Sort sort = Sort.by(Sort.Direction.DESC, "amount");

		final String accountIban = "ES9820385778983000760236";

		when(transactionRepository.findByAccountIban(accountIban, sort)).thenReturn(Optional.of(List.of(new Transaction())));

		List<Transaction> transactions = transactionService.findTransactionsByAccountIban(accountIban, sort);

		assertThat(transactions, is(notNullValue()));
		assertThat(transactions.size(), is(1));
	}

	@Test
	void whenNoTransactionsFound_shouldThrowTransactionNotFoundException() {
		final Sort sort = Sort.by(Sort.Direction.DESC, "amount");

		final String accountIban = "ES9820385778983000760236";

		when(transactionRepository.findByAccountIban(accountIban, sort)).thenReturn(Optional.empty());

		assertThrows(TransactionNotFoundException.class, () -> transactionService.findTransactionsByAccountIban(accountIban, sort));
	}


	@Test
	void findTransactionStatus_whenTransactionNotFound_shouldReturnTransactionWithStatusInvalid() {
//		Given: A transaction that is not stored in our system
//		When: I check the status from any channel
//		Then: The system returns the status 'INVALID'

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		when(transactionRepository.findById(reference)).thenReturn(Optional.empty());

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference, Channel.CLIENT);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getStatus(), is(Status.INVALID));
	}

	@ParameterizedTest
	@EnumSource(value = Channel.class, names = {"CLIENT", "ATM"})
	void findTransactionStatus_whenChannelClientOrAtm_shouldReturnTransactionStatusSettled(Channel channel) {
//		Given: A transaction that is stored in our system
//		When: I check the status from CLIENT or ATM channel
//			And the transaction date is before today
//		Then: The system returns the status 'SETTLED'
//			And the amount subtracting the fee

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.date(Instant.now().minus(3, ChronoUnit.DAYS))
						.build();

		when(transactionRepository.findById(reference)).thenReturn(Optional.of(transaction));

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference, channel);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getReference(), is(reference));
		assertThat(transactionStatus.getStatus(), is(Status.SETTLED));
		assertThat(transactionStatus.getAmount(), is(new BigDecimal("100.00")));
		assertThat(transactionStatus.getFee(), is(nullValue()));
	}

	@Test
	void findTransactionStatus_whenChannelInternal_shouldReturnTransactionStatusSettled() {
//		Given: A transaction that is stored in our system
//		When: I check the status from INTERNAL channel
//			And the transaction date is before today
//		Then: The system returns the status 'SETTLED'
//			And the amount
//			And the fee

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.date(Instant.now().minus(3, ChronoUnit.DAYS))
						.build();

		when(transactionRepository.findById(reference)).thenReturn(Optional.of(transaction));

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference,
						Channel.INTERNAL);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getReference(), is(reference));
		assertThat(transactionStatus.getStatus(), is(Status.SETTLED));
		assertThat(transactionStatus.getAmount(), is(new BigDecimal("100.25")));
		assertThat(transactionStatus.getFee(), is(new BigDecimal("0.25")));
	}

	@ParameterizedTest
	@EnumSource(value = Channel.class, names = {"CLIENT", "ATM"})
	void findTransactionStatus_whenChannelClientOrAtm_DateToday_shouldReturnTransactionStatusPending(Channel channel) {
//		Given: A transaction that is stored in our system
//		When: I check the status from CLIENT or ATM channel
//			And the transaction date is equals to today
//		Then: The system returns the status 'PENDING'
//			And the amount subtracting the fee

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.date(Instant.now())
						.build();

		when(transactionRepository.findById(reference)).thenReturn(Optional.of(transaction));

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference, channel);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getReference(), is(reference));
		assertThat(transactionStatus.getStatus(), is(Status.PENDING));
		assertThat(transactionStatus.getAmount(), is(new BigDecimal("100.00")));
		assertThat(transactionStatus.getFee(), is(nullValue()));
	}

	@Test
	void findTransactionStatus_whenChannelInternal_DateToday_shouldReturnTransactionStatusPending() {
//		Given: A transaction that is stored in our system
//		When: I check the status from INTERNAL channel
//			And the transaction date is equals to today
//		Then: The system returns the status 'PENDING'
//			And the amount
//			And the fee

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.date(Instant.now())
						.build();

		when(transactionRepository.findById(reference)).thenReturn(Optional.of(transaction));

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference, Channel.INTERNAL);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getReference(), is(reference));
		assertThat(transactionStatus.getStatus(), is(Status.PENDING));
		assertThat(transactionStatus.getAmount(), is(new BigDecimal("100.25")));
		assertThat(transactionStatus.getFee(), is(new BigDecimal("0.25")));
	}

	@Test
	void findTransactionStatus_whenChannelClientAndDateGreaterThanToday_shouldReturnStatusFuture() {
//		Given: A transaction that is stored in our system
//		When: I check the status from CLIENT channel
//			And the transaction date is greater than today
//		Then: The system returns the status 'FUTURE'
//			And the amount subtracting the fee

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.date(Instant.now().plus(3, ChronoUnit.DAYS))
						.build();

		when(transactionRepository.findById(reference)).thenReturn(Optional.of(transaction));

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference, Channel.CLIENT);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getStatus(), is(Status.FUTURE));
		assertThat(transactionStatus.getAmount(), is(new BigDecimal("100.00")));
		assertThat(transactionStatus.getReference(), is(reference));
		assertThat(transactionStatus.getFee(), is(nullValue()));
	}

	@Test
	void findTransactionStatus_whenChannelAtmAndDateGreaterThanToday_shouldReturnStatusPending() {
//		Given: A transaction that is stored in our system
//		When: I check the status from ATM channel
//			And the transaction date is greater than today
//		Then: The system returns the status 'PENDING'
//			And the amount substracting the fee

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.date(Instant.now().plus(3, ChronoUnit.DAYS))
						.build();

		when(transactionRepository.findById(reference)).thenReturn(Optional.of(transaction));

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference, Channel.ATM);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getReference(), is(reference));
		assertThat(transactionStatus.getStatus(), is(Status.PENDING));
		assertThat(transactionStatus.getAmount(), is(new BigDecimal("100.00")));
		assertThat(transactionStatus.getFee(), is(nullValue()));
	}

	@Test
	void findTransactionStatus_whenChannelInternal_DateGreaterThanToday_shouldReturnTransactionStatusFuture() {
//		Given: A transaction that is stored in our system
//		When: I check the status from INTERNAL channel
//			And the transaction date is greater than today
//		Then: The system returns the status 'FUTURE'
//			And the amount
//			And the fee

		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.amount(new BigDecimal("100.25"))
						.fee(new BigDecimal("0.25"))
						.date(Instant.now().plus(3, ChronoUnit.DAYS))
						.build();

		when(transactionRepository.findById(reference)).thenReturn(Optional.of(transaction));

		TransactionStatusResponseDTO transactionStatus = transactionService.findTransactionStatus(reference, Channel.INTERNAL);

		assertThat(transactionStatus, is(notNullValue()));
		assertThat(transactionStatus.getReference(), is(reference));
		assertThat(transactionStatus.getStatus(), is(Status.FUTURE));
		assertThat(transactionStatus.getAmount(), is(new BigDecimal("100.25")));
		assertThat(transactionStatus.getFee(), is(new BigDecimal("0.25")));
	}
}