package com.cristinelpavel.banktransactions.service.impl;

import com.cristinelpavel.banktransactions.exception.AccountNotFoundException;
import com.cristinelpavel.banktransactions.model.Account;
import com.cristinelpavel.banktransactions.repository.AccountRepository;
import com.cristinelpavel.banktransactions.service.AccountService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Validated
@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;

	@Override
	public Account findAccountById(@NotBlank String iban) {
		Optional<Account> account = accountRepository.findById(iban);

		return account.orElseThrow(() -> new AccountNotFoundException("Account with iban " + iban + " not found"));
	}

	@Override
	public Account updateAccount(@NotNull Account account) {
		return accountRepository.save(account);
	}
}
