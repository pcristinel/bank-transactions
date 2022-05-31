package com.cristinelpavel.banktransactions.repository;

import com.cristinelpavel.banktransactions.model.Transaction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author pcristinel
 * @since 0.0.1
 */
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
	Optional<List<Transaction>> findByAccountIban(String iban, Sort sort);
}