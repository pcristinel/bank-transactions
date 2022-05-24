package com.cristinelpavel.banktransactions.mapper;

import com.cristinelpavel.banktransactions.dto.TransactionDTO;
import com.cristinelpavel.banktransactions.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
	TransactionDTO toDto(Transaction transaction);
	Transaction toTransaction(TransactionDTO transaction);
}