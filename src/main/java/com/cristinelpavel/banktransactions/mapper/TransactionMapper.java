package com.cristinelpavel.banktransactions.mapper;

import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.dto.TransactionStatusResponseDTO;
import com.cristinelpavel.banktransactions.model.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {
	TransactionDTO toDto(Transaction transaction);

	Transaction toTransaction(TransactionDTO transaction);

	List<TransactionDTO> toDtoList(List<Transaction> byAccountIban);

	TransactionStatusResponseDTO toTransactionStatusResponseDTO(Transaction transaction);
}