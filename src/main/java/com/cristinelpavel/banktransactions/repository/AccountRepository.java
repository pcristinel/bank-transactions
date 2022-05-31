package com.cristinelpavel.banktransactions.repository;

import com.cristinelpavel.banktransactions.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author pcristinel
 * @since 0.0.1
 */
public interface AccountRepository extends JpaRepository<Account, String> {
}