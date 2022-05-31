package com.cristinelpavel.banktransactions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionStatusResponseDTO {
	public enum Status {
		PENDING,
		SETTLED,
		FUTURE,
		INVALID
	}

	private UUID reference;
	private Status status;
	private BigDecimal amount;
	private BigDecimal fee;
}