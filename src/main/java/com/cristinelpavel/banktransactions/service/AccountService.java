package com.cristinelpavel.banktransactions.service;

import com.cristinelpavel.banktransactions.model.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author pcristinel
 * @since 0.0.1
 */
public interface AccountService {

  Account findAccountById(@NotBlank String iban);

  Account updateAccount(@NotNull Account account);
}
