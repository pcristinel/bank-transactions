package com.cristinelpavel.banktransactions.controller.impl;

import com.cristinelpavel.banktransactions.controller.TransactionController;
import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.exception.AccountNotFoundException;
import com.cristinelpavel.banktransactions.mapper.TransactionMapper;
import com.cristinelpavel.banktransactions.model.Transaction;
import com.cristinelpavel.banktransactions.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
	@MockBean
	private TransactionService transactionService;

	@MockBean
	private TransactionMapper transactionMapper;

	@Autowired
	private MockMvc mockMvc;


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

}