package com.cristinelpavel.banktransactions.controller.impl;

import com.cristinelpavel.banktransactions.controller.TransactionController;
import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.exception.AccountNotFoundException;
import com.cristinelpavel.banktransactions.exception.TransactionNotFoundException;
import com.cristinelpavel.banktransactions.mapper.TransactionMapper;
import com.cristinelpavel.banktransactions.model.Transaction;
import com.cristinelpavel.banktransactions.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@WebMvcTest(TransactionController.class)
class TransactionControllerImplTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TransactionService transactionService;

	@MockBean
	private TransactionMapper transactionMapper;

	@Test
	void shouldCreateTransaction() throws Exception {
		final UUID reference = UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002");

		Transaction transaction = Transaction.builder()
						.reference(reference)
						.accountIban("ES9820385778983000760236")
						.date(Instant.parse("2019-07-16T16:55:42.000Z"))
						.amount(BigDecimal.valueOf(193.38))
						.fee(BigDecimal.valueOf(3.18))
						.description("Restaurant payment")
						.build();


		TransactionDTO transactionDTO = TransactionDTO.builder()
						.reference(reference)
						.accountIban("ES9820385778983000760236")
						.date(Instant.parse("2019-07-16T16:55:42.000Z"))
						.amount(BigDecimal.valueOf(193.38))
						.fee(BigDecimal.valueOf(3.18))
						.description("Restaurant payment")
						.build();

		when(transactionMapper.toTransaction(any(TransactionDTO.class))).thenReturn(transaction);
		when(transactionService.saveTransaction(any(Transaction.class))).thenReturn(transaction);
		when(transactionMapper.toDto(transaction)).thenReturn(transactionDTO);

		mockMvc.perform(post("/api/v1/transactions")
										.contentType(MediaType.APPLICATION_JSON)
										.content("""
																{
																	"reference": "97835a0a-e05c-11ec-9d64-0242ac120002",
																	"account_iban": "ES9820385778983000760236",
																	"date": "2019-07-16T16:55:42.000Z",
																	"amount": 193.38,
																	"fee": 3.18,
																	"description": "Restaurant payment"
																}
														"""))
						.andExpect(status().isCreated())
						.andExpect(header().exists("Location"))
						.andExpect(header().string("Location", containsString(reference.toString())))
						.andExpect(jsonPath("$.reference").value(reference.toString()))
						.andExpect(jsonPath("$.account_iban").value("ES9820385778983000760236"))
						.andExpect(jsonPath("$.date").value("2019-07-16T16:55:42Z"))
						.andExpect(jsonPath("$.amount").value(193.38))
						.andExpect(jsonPath("$.fee").value(3.18))
						.andExpect(jsonPath("$.description").value("Restaurant payment"));

		verify(transactionService).saveTransaction(any(Transaction.class));
		verify(transactionMapper).toDto(transaction);
	}

	@Test
	void shouldReturnAccountNotFoundException() throws Exception {
		when(transactionMapper.toTransaction(any(TransactionDTO.class))).thenReturn(new Transaction());
		when(transactionService.saveTransaction(any(Transaction.class))).thenThrow(AccountNotFoundException.class);

		mockMvc.perform(post("/api/v1/transactions")
										.contentType(MediaType.APPLICATION_JSON)
										.content("""
																{
																	"reference": "97835a0a-e05c-11ec-9d64-0242ac120002",
																	"account_iban": "ES9820385778983000760236",
																	"date": "2019-07-16T16:55:42.000Z",
																	"amount": 193.38,
																	"fee": 3.18,
																	"description": "Restaurant payment"
																}
														"""))
						.andExpect(status().isNotFound())
						.andExpect(result -> assertTrue(result.getResolvedException() instanceof AccountNotFoundException));
	}

	@Test
	void shouldReturnListOfTransactionsSortedByAmountDesc() throws Exception {
		Transaction transaction1 = Transaction.builder()
						.reference(UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002"))
						.accountIban("ES9820385778983000760236")
						.date(Instant.parse("2019-07-16T17:55:42.000Z"))
						.amount(BigDecimal.valueOf(198.38))
						.fee(BigDecimal.valueOf(3.18))
						.description("Restaurant payment")
						.build();

		TransactionDTO transactionDTO1 = TransactionDTO.builder()
						.reference(UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120002"))
						.accountIban("ES9820385778983000760236")
						.date(Instant.parse("2019-07-16T17:55:42.000Z"))
						.amount(BigDecimal.valueOf(198.38))
						.fee(BigDecimal.valueOf(3.18))
						.description("Restaurant payment")
						.build();


		Transaction transaction2 = Transaction.builder()
						.reference(UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120003"))
						.accountIban("ES9820385778983000760236")
						.date(Instant.parse("2019-07-16T16:55:42.000Z"))
						.amount(BigDecimal.valueOf(193.38))
						.fee(BigDecimal.valueOf(3.18))
						.description("Restaurant payment")
						.build();

		TransactionDTO transactionDTO2 = TransactionDTO.builder()
						.reference(UUID.fromString("97835a0a-e05c-11ec-9d64-0242ac120003"))
						.accountIban("ES9820385778983000760236")
						.date(Instant.parse("2019-07-16T16:55:42.000Z"))
						.amount(BigDecimal.valueOf(193.38))
						.fee(BigDecimal.valueOf(3.18))
						.description("Restaurant payment")
						.build();

		List<Transaction> transactions = Arrays.asList(transaction1, transaction2);

		when(transactionService.findTransactionsByAccountIban(anyString(), any(Sort.class))).thenReturn(transactions);

		when(transactionMapper.toDtoList(transactions)).thenReturn(List.of(transactionDTO1, transactionDTO2));

		mockMvc.perform(get("/api/v1/transactions/search?account_iban=ES9820385778983000760236&sortField=amount&sortOrder=desc"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.size()", is(2)))
						.andExpect(jsonPath("$[0].reference").value("97835a0a-e05c-11ec-9d64-0242ac120002"))
						.andExpect(jsonPath("$[0].amount").value(198.38))
						.andExpect(jsonPath("$[1].reference").value("97835a0a-e05c-11ec-9d64-0242ac120003"))
						.andExpect(jsonPath("$[1].amount").value(193.38));

	}

	@Test
	void shouldReturn404ResourcesNotFound() throws Exception {
		when(transactionService.findTransactionsByAccountIban(anyString(), any(Sort.class))).thenThrow(new TransactionNotFoundException("No transactions found for account iban ES9820385778983000760236"));

		mockMvc.perform(get("/api/v1/transactions/search?account_iban=ES9820385778983000760236&sortField=amount&sortOrder=desc"))
						.andExpect(status().isNotFound())
						.andExpect(result -> assertTrue(result.getResolvedException() instanceof TransactionNotFoundException));
	}


}