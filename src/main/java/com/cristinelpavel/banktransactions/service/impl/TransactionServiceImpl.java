package com.cristinelpavel.banktransactions.service.impl;

import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.mapper.TransactionMapper;
import com.cristinelpavel.banktransactions.model.Transaction;
import com.cristinelpavel.banktransactions.repository.TransactionRepository;
import com.cristinelpavel.banktransactions.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
	private final TransactionMapper transactionMapper;

	@Override
	public TransactionDTO saveTransaction(TransactionDTO transactionDTO) {
		Transaction savedTransaction = transactionRepository.save(transactionMapper.toTransaction(transactionDTO));

		return transactionMapper.toDto(savedTransaction);
	}
}