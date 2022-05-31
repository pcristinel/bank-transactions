package com.cristinelpavel.banktransactions.service.impl;

import com.cristinelpavel.banktransactions.exception.AccountNotFoundException;
import com.cristinelpavel.banktransactions.model.Account;
import com.cristinelpavel.banktransactions.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class AccountServiceImplTest {

	@InjectMocks
	private AccountServiceImpl accountService;

	@Mock
	private AccountRepository accountRepository;

	@Test
	void findAccountById_ExistingAccountShouldReturnAccount() {
		Account accountFromRepository = Account.builder()
						.iban("ES9820385778983000760236")
						.accountBalance(new BigDecimal("1000.00"))
						.build();

		when(accountRepository.findById(anyString())).thenReturn(Optional.of(accountFromRepository));

		Account account = accountService.findAccountById("ES9820385778983000760236");

		assertThat(account, is(notNullValue()));
		assertThat(account.getIban(), is("ES9820385778983000760236"));
	}

	@Test
	void findAccountById_NonExistingAccountShouldThrowAccountNotFoundException() {
		when(accountRepository.findById(anyString())).thenReturn(Optional.empty());

		assertThrows(AccountNotFoundException.class, () -> accountService.findAccountById("ES9820385778983000760236"));
	}

}