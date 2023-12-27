package com.cristinelpavel.banktransactions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

/**
 * @author pcristinel
 * @since 0.0.1
 */
@Data
@Builder
public class TransactionDTO {
	private UUID reference;

	@NotEmpty
	@JsonProperty("account_iban")
	private String accountIban;

	private Instant date;

	@NotNull
	private BigDecimal amount;

	private BigDecimal fee;

	private String description;
}
