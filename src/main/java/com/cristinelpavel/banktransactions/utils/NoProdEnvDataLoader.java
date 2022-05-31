package com.cristinelpavel.banktransactions.utils;

import com.cristinelpavel.banktransactions.model.Account;
import com.cristinelpavel.banktransactions.model.Transaction;
import com.cristinelpavel.banktransactions.repository.AccountRepository;
import com.cristinelpavel.banktransactions.service.TransactionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
@Profile("!prod")
public class NoProdEnvDataLoader {
	@Bean
	public CommandLineRunner accountsDataLoader(AccountRepository accountRepository) {
		return args -> {
			accountRepository.save(new Account("ES8001288958117798186521", BigDecimal.valueOf(1890.57)));
			accountRepository.save(new Account("ES7020387369561296863989", BigDecimal.valueOf(9012.17)));
			accountRepository.save(new Account("ES3730046235659186738712", BigDecimal.valueOf(2590.12)));
			accountRepository.save(new Account("ES4700819583172182554945", BigDecimal.valueOf(124.01)));
			accountRepository.save(new Account("ES6530048559391994527242", BigDecimal.valueOf(5.00)));
			accountRepository.save(new Account("ES6401281231733399171787", BigDecimal.valueOf(200.68)));
			accountRepository.save(new Account("ES9820385778983000760236", BigDecimal.valueOf(250.38)));
		};
	}

	@Bean
	public CommandLineRunner transactionsDataLoader(TransactionService transactionService) {
		return args -> {
			transactionService.saveTransaction(Transaction.builder()
							.accountIban("ES3730046235659186738712")
							.date(Instant.parse("2022-04-11T22:56:12Z"))
							.amount(BigDecimal.valueOf(-91.15))
							.fee(BigDecimal.valueOf(3.12))
							.description("Dinner at La Marina")
							.build());


			transactionService.saveTransaction(Transaction.builder()
							.accountIban("ES3730046235659186738712")
							.date(Instant.parse("2022-07-01T05:58:17Z"))
							.amount(BigDecimal.valueOf(90.12))
							.fee(BigDecimal.valueOf(2.00))
							.description("Lunch at La Tajada")
							.build());

			transactionService.saveTransaction(Transaction.builder()
							.accountIban("ES4700819583172182554945")
							.date(Instant.now())
							.amount(BigDecimal.valueOf(4.01))
							.description("Mercadona")
							.build());

			transactionService.saveTransaction(Transaction.builder()
							.accountIban("ES4700819583172182554945")
							.date(Instant.now())
							.amount(BigDecimal.valueOf(21.05))
							.fee(BigDecimal.valueOf(1.05))
							.description("Mercadona with fee")
							.build());

			transactionService.saveTransaction(Transaction.builder()
							.accountIban("ES6401281231733399171787")
							.date(Instant.now())
							.amount(BigDecimal.valueOf(0.68))
							.description("Taco Bell")
							.build());
		};
	}
}