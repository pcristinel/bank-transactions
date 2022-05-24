package com.cristinelpavel.banktransactions.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Data
@Builder
public class TransactionDTO {
	private Long reference;

	@NotEmpty
	private String accountIban;

	private Instant date;

	@NotNull
	private Double amount;

	private Double fee;

	private String description;
}